package br.edu.ifrs.hortafacil.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Resource;

import br.edu.ifrs.hortafacil.Constants;
import br.edu.ifrs.hortafacil.model.cultivars.Cultivar;
import br.edu.ifrs.hortafacil.model.cultivars.CultivarID;
import br.edu.ifrs.hortafacil.model.harvest.Harvest;
import br.edu.ifrs.hortafacil.model.management.Management;
import br.edu.ifrs.hortafacil.model.management.ManagementPractice;
import br.edu.ifrs.hortafacil.model.management.planting.CultivarPlantingSignature;
import br.edu.ifrs.hortafacil.model.management.planting.PlantingMethod;
import br.edu.ifrs.hortafacil.model.prefs.CultivarPreferenceSignature;
import br.edu.ifrs.hortafacil.util.DateUtil;
import br.edu.ifrs.hortafacil.util.RDFUtil;

/**
 * DAO (Data Access Object) responsável por montar os objetos {@link Cultivar}
 * a partir do triplestore RDF (Apache Jena Fuseki): identificação,
 * preferências (temperatura, pH, luz, água, drenagem), informações de plantio
 * (espaçamentos, métodos, práticas, meses) e manejos.
 */
public class CultivarsDAO {

	/** Nome local válido: apenas letras, dígitos, "_" e "-". */
	private static final Pattern LOCAL_NAME = Pattern.compile("[A-Za-z0-9_\\-]+");

	/**
	 * Mapa nome local do mês na ontologia (ex.: "January") para o número do mês
	 * (1 a 12). Construído a partir de {@link DateUtil#MONTHS_URIS}, cujos itens
	 * têm o formato "conto:NomeDoMes".
	 */
	private static final Map<String, Integer> MONTH_NUMBERS = buildMonthNumbers();

	/**
	 * Consulta principal: cultivares que fornecem um produto e podem ser
	 * plantadas em um mês e estado.
	 * <p>
	 * Observações:
	 * <ul>
	 * <li>{@code ?productClass}, {@code ?monthIri} e {@code ?stateAbbr} são
	 * injetados de forma segura via {@link ParameterizedSparqlString} (antes
	 * eram concatenados no texto da consulta).</li>
	 * <li>Os blocos {@code OPTIONAL} deixam as preferências e os espaçamentos
	 * como opcionais; quando ausentes, as variáveis ficam sem valor.</li>
	 * <li>Os filtros de idioma continuam exigindo exatamente "pt-BR".</li>
	 * <li>O ORDER BY dá uma ordem estável ao resultado.</li>
	 * <li>Requer Java 15+ por causa do text block.</li>
	 * </ul>
	 */
	private static final String CULTIVARS_QUERY_BY_PRODUCT = """
			PREFIX conto: <%s>
			PREFIX skos:  <http://www.w3.org/2004/02/skos/core#>
			PREFIX dul:   <http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#>
			PREFIX time:  <http://www.w3.org/2006/time#>
			PREFIX qudt:  <http://purl.org/qudt/schema/qudt/>

			SELECT DISTINCT
			  ?cultResource ?cultName
			  ?tempMin ?tempMax ?PHMin ?PHMax ?lightPref ?waterPref ?soilPref
			  ?rowSpacing ?rowSpacingUnit ?plantSpacing ?plantSpacingUnit ?pMngt
			WHERE {
			  # Cultivares que fornecem o produto informado
			  ?cultResource a conto:Cultivar ;
			                conto:providesProduct ?prod ;
			                skos:prefLabel ?cultName ;
			                conto:hasManagement ?pMngt .
			  ?prod a ?productClass .

			  # Manejo de plantio com época (mês) e região (estado) informados
			  ?pMngt conto:hasPlantingSeason ?season .
			  ?season dul:hasLocation ?reg ;
			          time:hasTime ?monthIri .
			  ?reg dul:hasPart ?estado .
			  ?estado skos:altLabel ?stateAbbr .

			  # Preferência de temperatura
			  OPTIONAL {
			    ?cultResource conto:hasPreference ?Tpref .
			    ?Tpref a conto:TemperaturePreference ;
			           qudt:hasQuantity ?Tquant .
			    ?Tquant qudt:quantityValue ?Tqvalue .
			    ?Tqvalue qudt:minInclusive ?tempMin ;
			             qudt:maxInclusive ?tempMax .
			  }

			  # Preferência de pH do solo
			  OPTIONAL {
			    ?cultResource conto:hasPreference ?Ppref .
			    ?Ppref a conto:SoilpHPreference ;
			           qudt:hasQuantity ?PHquant .
			    ?PHquant qudt:quantityValue ?PHqvalue .
			    ?PHqvalue qudt:minInclusive ?PHMin ;
			              qudt:maxInclusive ?PHMax .
			  }

			  # Preferência de luminosidade
			  OPTIONAL {
			    ?cultResource conto:hasPreference ?Lpref .
			    ?Lpref a conto:LightPreference ;
			           conto:name ?lightPref .
			    FILTER(LANG(?lightPref) = "pt-BR")
			  }

			  # Preferência de água
			  OPTIONAL {
			    ?cultResource conto:hasPreference ?Wpref .
			    ?Wpref a conto:WaterPreference ;
			           conto:name ?waterPref .
			    FILTER(LANG(?waterPref) = "pt-BR")
			  }

			  # Preferência de drenagem do solo
			  OPTIONAL {
			    ?cultResource conto:hasPreference ?Spref .
			    ?Spref a conto:SoilDrainagePreference ;
			           conto:name ?soilPref .
			    FILTER(LANG(?soilPref) = "pt-BR")
			  }

			  # Espaçamento entre linhas
			  OPTIONAL {
			    ?pMngt conto:rowSpacing ?qRS .
			    ?qRS qudt:quantityValue ?qvalRS .
			    ?qvalRS qudt:hasUnit ?rowSpacingUnit ;
			            qudt:numericValue ?rowSpacing .
			  }

			  # Espaçamento entre plantas
			  OPTIONAL {
			    ?pMngt conto:plantSpacing ?qPS .
			    ?qPS qudt:quantityValue ?qvalPS .
			    ?qvalPS qudt:hasUnit ?plantSpacingUnit ;
			            qudt:numericValue ?plantSpacing .
			  }
			}
			ORDER BY ?cultName ?cultResource ?pMngt
			""".formatted(Constants.CONTO_NS);
	
	private static final String CULTIVAR_QUERY = """
			PREFIX conto: <%s>
			PREFIX skos:  <http://www.w3.org/2004/02/skos/core#>
			PREFIX dul:   <http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#>
			PREFIX time:  <http://www.w3.org/2006/time#>
			PREFIX qudt:  <http://purl.org/qudt/schema/qudt/>

			SELECT DISTINCT
			  ?cultResource ?cultName
			  ?tempMin ?tempMax ?PHMin ?PHMax ?lightPref ?waterPref ?soilPref
			  ?rowSpacing ?rowSpacingUnit ?plantSpacing ?plantSpacingUnit ?pMngt
			WHERE {
			  VALUES ?cultResource { ?cultivar }
			  # Cultivares que fornecem o produto informado
			  ?cultResource a conto:Cultivar ;
			                skos:prefLabel ?cultName ;
			                conto:hasManagement ?pMngt .
			                
			  ?pMngt conto:hasPlantingSeason ?season .
			  
			  # Preferência de temperatura
			  OPTIONAL {
			    ?cultResource conto:hasPreference ?Tpref .
			    ?Tpref a conto:TemperaturePreference ;
			           qudt:hasQuantity ?Tquant .
			    ?Tquant qudt:quantityValue ?Tqvalue .
			    ?Tqvalue qudt:minInclusive ?tempMin ;
			             qudt:maxInclusive ?tempMax .
			  }

			  # Preferência de pH do solo
			  OPTIONAL {
			    ?cultResource conto:hasPreference ?Ppref .
			    ?Ppref a conto:SoilpHPreference ;
			           qudt:hasQuantity ?PHquant .
			    ?PHquant qudt:quantityValue ?PHqvalue .
			    ?PHqvalue qudt:minInclusive ?PHMin ;
			              qudt:maxInclusive ?PHMax .
			  }

			  # Preferência de luminosidade
			  OPTIONAL {
			    ?cultResource conto:hasPreference ?Lpref .
			    ?Lpref a conto:LightPreference ;
			           conto:name ?lightPref .
			    FILTER(LANG(?lightPref) = "pt-BR")
			  }

			  # Preferência de água
			  OPTIONAL {
			    ?cultResource conto:hasPreference ?Wpref .
			    ?Wpref a conto:WaterPreference ;
			           conto:name ?waterPref .
			    FILTER(LANG(?waterPref) = "pt-BR")
			  }

			  # Preferência de drenagem do solo
			  OPTIONAL {
			    ?cultResource conto:hasPreference ?Spref .
			    ?Spref a conto:SoilDrainagePreference ;
			           conto:name ?soilPref .
			    FILTER(LANG(?soilPref) = "pt-BR")
			  }

			  # Espaçamento entre linhas
			  OPTIONAL {
			    ?pMngt conto:rowSpacing ?qRS .
			    ?qRS qudt:quantityValue ?qvalRS .
			    ?qvalRS qudt:hasUnit ?rowSpacingUnit ;
			            qudt:numericValue ?rowSpacing .
			  }

			  # Espaçamento entre plantas
			  OPTIONAL {
			    ?pMngt conto:plantSpacing ?qPS .
			    ?qPS qudt:quantityValue ?qvalPS .
			    ?qvalPS qudt:hasUnit ?plantSpacingUnit ;
			            qudt:numericValue ?plantSpacing .
			  }
			}
			ORDER BY ?cultName ?cultResource ?pMngt
			""".formatted(Constants.CONTO_NS);

	/**
	 * Consulta dos meses de plantio de uma cultivar em um estado.
	 * <p>
	 * {@code ?cultivar} e {@code ?stateAbbr} são injetados via
	 * {@link ParameterizedSparqlString}. {@code DISTINCT} evita meses repetidos.
	 */
	private static final String MONTHS_QUERY = """
			PREFIX time:  <http://www.w3.org/2006/time#>
	        PREFIX conto: <%s>
	        PREFIX dul:   <http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#>
	        PREFIX skos:  <http://www.w3.org/2004/02/skos/core#>
	
	        SELECT DISTINCT ?cultivar ?month WHERE {
	          VALUES ?cultivar { %s }
	          ?cultivar conto:hasManagement ?plantMgmt .
	          ?plantMgmt a conto:PlantingManagement ;
	                     conto:hasPlantingSeason ?season .
	          ?season dul:hasLocation ?reg ;
	                  time:hasTime ?month .
	          ?reg dul:hasPart ?estado .
	          ?estado skos:altLabel ?stateAbbr .
	        }
			""";

	// DAOs auxiliares injetados pelo construtor (facilita testes com mocks)
	private final PlantingMethodDAO plantingMethodDAO;
	private final ManagementPracticeDAO practiceDAO;
	private final ManagementDAO managementDAO;
	private final HarvestDAO harvestDAO;

	/** Construtor padrão: usa as implementações reais dos DAOs auxiliares. */
	public CultivarsDAO() {
		this(new PlantingMethodDAO(), new ManagementPracticeDAO(), new ManagementDAO(), new HarvestDAO());
	}

	/** Construtor para injeção de dependências (útil em testes). */
	public CultivarsDAO(PlantingMethodDAO plantingMethodDAO, ManagementPracticeDAO practiceDAO,
			ManagementDAO managementDAO, HarvestDAO harvestDAO) {
		this.plantingMethodDAO = plantingMethodDAO;
		this.practiceDAO = practiceDAO;
		this.managementDAO = managementDAO;
		this.harvestDAO = harvestDAO;
	}

	/**
	 * Busca as cultivares de um produto que podem ser plantadas em um mês e
	 * estado.
	 *
	 * @param productResource nome local da classe do produto na ontologia (ex.:
	 *                        "Tomato"); apenas letras, dígitos, "_" e "-"
	 * @param month           mês do plantio, de 1 (janeiro) a 12 (dezembro)
	 * @param state           sigla do estado (ex.: "RS")
	 * @return lista de cultivares ordenada pelo nome; vazia se não houver
	 * @throws IllegalArgumentException se algum parâmetro for inválido
	 * @throws IllegalStateException    se houver falha ao consultar o Fuseki
	 */
	public List<Cultivar> getCultivarsByProduct(String productResource, int month, String state) {
		validateLocalName(productResource, "produto");
		validateState(state);
		String monthIri = monthIri(month);

		// Injeta os valores como IRI/literal: o Jena cuida do escape, impedindo
		// que um valor malicioso altere a estrutura da consulta
		ParameterizedSparqlString pss = new ParameterizedSparqlString(CULTIVARS_QUERY_BY_PRODUCT);
		pss.setIri("productClass", Constants.CONTO_NS + productResource);
		pss.setIri("monthIri", monthIri);
		pss.setLiteral("stateAbbr", state);

		// O resultado é lido por completo e a conexão é fechada ANTES do
		// carregamento dos dados aninhados, que abrem as próprias conexões.
		// Assim não há conexões aninhadas nem leitura preguiçosa do stream.
		List<QuerySolution> rows = DAO.runSelect(pss.asQuery(),
				"Erro ao consultar as cultivares do produto '" + productResource + "'");

		// Coleta todas as URIs únicas obtidas
	    Set<String> cultivarUris = new HashSet<>();
	    Set<String> managementUris = new HashSet<>();
	    for (QuerySolution soln : rows) {
	        cultivarUris.add(soln.getResource("cultResource").getURI());
	        managementUris.add(soln.getResource("pMngt").getURI());
	    }
	    
	    // 2. Executa consultas em LOTE para preencher os dados associados (3 a 4 requisições no total)
	    Map<String, List<Integer>> monthsMap = getMonthsToPlantByCultivars(cultivarUris, state);
	    Map<String, List<PlantingMethod>> methodsMap = plantingMethodDAO.getPlantingMethodsByManagements(managementUris);
	    Map<String, List<ManagementPractice>> practicesMap = practiceDAO.getMngtPracticesByManagements(managementUris);
	    Map<String, List<Management>> managementsMap = managementDAO.getManagementsByCultivars(cultivarUris);
	    Map<String, List<Harvest>> harvestMap = harvestDAO.getHarvestsByCultivars(cultivarUris);
	    
 		// Descarta linhas repetidas para o mesmo par cultivar + manejo de plantio
 		Set<String> seen = new HashSet<>();
		
		List<Cultivar> cultivars = new ArrayList<>();
		for (QuerySolution soln : rows) {
			String cultUri = soln.getResource("cultResource").getURI();
			String mngtUri = soln.getResource("pMngt").getURI();

			if (!seen.add(cultUri + "|" + mngtUri)) {
				continue;
			}

			cultivars.add(buildCultivar(soln, cultUri, mngtUri, monthsMap, methodsMap, practicesMap, managementsMap, harvestMap));
		}

		return cultivars;
	}
	
	/**
	 * Busca informações sobre o cultivar especificado no parâmetro, relacionados ao estado informado.
	 * 
	 * @param resource nome local da classe do cultivar na ontologia (ex.:
	 *                 "cv_Itapua_202"); apenas letras, dígitos, "_" e "-"
	 * @param state sigla do estado (ex.: "RS")
	 * @return objeto contendo os dados do cultivar encontrado ou null, caso o cultivar não seja encontrado.
	 */
	public Cultivar getCultivar(String resource, String state) {
		validateLocalName(resource, "cultivar");
		
		// Injeta os valores como IRI/literal: o Jena cuida do escape, impedindo
		// que um valor malicioso altere a estrutura da consulta
		ParameterizedSparqlString pss = new ParameterizedSparqlString(CULTIVAR_QUERY);
		pss.setIri("cultivar", Constants.CONTO_NS + resource);
		
		// O resultado é lido por completo e a conexão é fechada ANTES do
		// carregamento dos dados aninhados, que abrem as próprias conexões.
		// Assim não há conexões aninhadas nem leitura preguiçosa do stream.
		List<QuerySolution> rows = DAO.runSelect(pss.asQuery(),
				"Erro ao consultar o cultivar '" + resource + "'");

		if(rows.isEmpty())
			return null;
		
		QuerySolution soln = rows.get(0);
		
		// Coleta todas as URIs únicas obtidas
	    String cultUri = soln.getResource("cultResource").getURI();
	    String mngtUri = soln.getResource("pMngt").getURI();
	    
	    Set<String> cultivarUris = new HashSet<String>();
	    cultivarUris.add(cultUri);
	    Set<String> managementUris = new HashSet<String>();
	    managementUris.add(mngtUri);
		
	    // 2. Executa consultas em LOTE para preencher os dados associados (3 a 4 requisições no total)
	    Map<String, List<Integer>> monthsMap = getMonthsToPlantByCultivars(cultivarUris, state);
	    Map<String, List<PlantingMethod>> methodsMap = plantingMethodDAO.getPlantingMethodsByManagements(managementUris);
	    Map<String, List<ManagementPractice>> practicesMap = practiceDAO.getMngtPracticesByManagements(managementUris);
	    Map<String, List<Management>> managementsMap = managementDAO.getManagementsByCultivars(cultivarUris);
	    Map<String, List<Harvest>> harvestMap = harvestDAO.getHarvestsByCultivars(cultivarUris);
	    
 		return buildCultivar(soln, cultUri, mngtUri, monthsMap, methodsMap, practicesMap, managementsMap, harvestMap);
	}

	/**
	 * Busca os meses em que as cultivares informadas podem ser plantadas em um estado.
	 *
	 * @param cultivarUris URIs absolutas dos recursos das cultivares desejadas.
	 * @param state        sigla do estado (ex.: "RS")
	 * @return um map associando Uris de cultivares a uma lista de números dos meses (1 a 12), sem repetições e em ordem crescente
	 * @throws IllegalStateException    se houver falha ao consultar o Fuseki ou
	 *                                  se um mês desconhecido for encontrado
	 */
	private Map<String, List<Integer>> getMonthsToPlantByCultivars(Set<String> cultivarUris, String state) {
	    if (cultivarUris.isEmpty()) return Map.of();

	    String uriList = DAO.stringifyURISet(cultivarUris);
	    	
		ParameterizedSparqlString pss = new ParameterizedSparqlString(MONTHS_QUERY.formatted(Constants.CONTO_NS, uriList));
		pss.setLiteral("stateAbbr", state);
		
		List<QuerySolution> rows = DAO.runSelect(pss.asQuery(),
				"Erro ao consultar os meses de plantio para os cultivares (" + uriList + ")");
		
		// 2. Agrupa os resultados por URI da cultivar
	    Map<String, List<Integer>> resultMap = new HashMap<>();
	    for (QuerySolution soln : rows) {
	        String cultUri = soln.getResource("cultivar").getURI();
	        String localName = soln.getResource("month").getLocalName();
	        Integer monthNum = MONTH_NUMBERS.get(localName);

	        if (monthNum != null) {
	            resultMap.computeIfAbsent(cultUri, k -> new ArrayList<>()).add(monthNum);
	        }
	    }
	    return resultMap;
	}
	
	/** Monta o objeto {@link Cultivar} a partir de uma linha do resultado. */
	private Cultivar buildCultivar(QuerySolution soln, String cultUri, String mngtUri,
			Map<String, List<Integer>> monthsMap, Map<String, List<PlantingMethod>> methodsMap, 
			Map<String, List<ManagementPractice>> practicesMap, Map<String, List<Management>> managementsMap,
			Map<String, List<Harvest>> harvestMap) {

		Cultivar c = new Cultivar();

		// Identificação
		CultivarID desc = new CultivarID();
		desc.setResource(cultUri);
		desc.setName(soln.getLiteral("cultName").getString());
		c.setDescription(desc);

		// Preferências. Todas são opcionais na consulta, então as variáveis
		// podem estar sem valor: o RDFUtil precisa aceitar literais nulos
		CultivarPreferenceSignature prefs = new CultivarPreferenceSignature();
		prefs.setTempMin(RDFUtil.getDoubleLiteral(soln.getLiteral("tempMin")));
		prefs.setTempMax(RDFUtil.getDoubleLiteral(soln.getLiteral("tempMax")));
		prefs.setPhMin(RDFUtil.getDoubleLiteral(soln.getLiteral("PHMin")));
		prefs.setPhMax(RDFUtil.getDoubleLiteral(soln.getLiteral("PHMax")));
		prefs.setLuminosity(RDFUtil.getStringLiteral(soln.getLiteral("lightPref")));
		prefs.setWater(RDFUtil.getStringLiteral(soln.getLiteral("waterPref")));
		prefs.setSoilDrenage(RDFUtil.getStringLiteral(soln.getLiteral("soilPref")));
		c.setPreferences(prefs);

		// Informações de plantio
		CultivarPlantingSignature plantingInfo = new CultivarPlantingSignature();
		plantingInfo.setMonthsToPlant(monthsMap.get(cultUri));
		plantingInfo.setPlantSpacing(toLength(soln.getLiteral("plantSpacing"),
				soln.getResource("plantSpacingUnit"), "espaçamento entre plantas"));
		plantingInfo.setRowSpacing(toLength(soln.getLiteral("rowSpacing"),
				soln.getResource("rowSpacingUnit"), "espaçamento entre linhas"));
		plantingInfo.setMethods(methodsMap.get(mngtUri));
		plantingInfo.setPractices(practicesMap.get(mngtUri));
		c.setPlantingInfo(plantingInfo);

		// Manejos da cultivar
		c.setManagement(managementsMap.get(cultUri));
		
		// Colheitas do cultivar
		c.setHarvest(harvestMap.get(cultUri));

		return c;
	}

	/**
	 * Converte valor e unidade lidos do RDF em {@code Quantity<Length>}.
	 * <p>
	 * Retorna {@code null} quando o valor ou a unidade não existem (campos
	 * opcionais). O cast para {@code Quantity<Length>} não é verificável pelo
	 * compilador; por isso a unidade é validada com
	 * {@code Unit.asType(Length.class)} e, se não for de comprimento, o método
	 * falha com uma mensagem clara.
	 */
	@SuppressWarnings("unchecked")
	private static Quantity<Length> toLength(Literal value, Resource unit, String description) {
		if (value == null || unit == null) {
			return null;
		}

		Quantity<?> quantity = RDFUtil.getQuantity(value, unit);
		if (quantity == null) {
			return null;
		}

		try {
			quantity.getUnit().asType(Length.class);
		} catch (ClassCastException e) {
			throw new IllegalStateException("A unidade <" + unit.getURI() + "> do " + description
					+ " não representa comprimento.", e);
		}

		return (Quantity<Length>) quantity;
	}

	/**
	 * Valida o mês e devolve o IRI correspondente na ontologia, a partir de
	 * {@link DateUtil#MONTHS_URIS} (itens no formato "conto:NomeDoMes").
	 */
	private static String monthIri(int month) {
		if (month < 1 || month > DateUtil.MONTHS_URIS.length) {
			throw new IllegalArgumentException(
					"Mês inválido: " + month + " (esperado de 1 a " + DateUtil.MONTHS_URIS.length + ")");
		}
		return Constants.CONTO_NS + localName(DateUtil.MONTHS_URIS[month - 1]);
	}

	/** Remove o prefixo ("conto:") de um nome prefixado. */
	private static String localName(String prefixedName) {
		return prefixedName.substring(prefixedName.indexOf(':') + 1);
	}

	/** Monta o mapa nome local do mês para número do mês (1 a 12). */
	private static Map<String, Integer> buildMonthNumbers() {
		Map<String, Integer> map = new HashMap<>();
		for (int i = 0; i < DateUtil.MONTHS_URIS.length; i++) {
			map.put(localName(DateUtil.MONTHS_URIS[i]), i + 1);
		}
		return map;
	}

	/** Garante que o valor é um nome local simples (sem espaços ou símbolos). */
	private static void validateLocalName(String value, String description) {
		if (value == null || !LOCAL_NAME.matcher(value).matches()) {
			throw new IllegalArgumentException("Valor inválido para " + description + ": " + value);
		}
	}

	/** Garante que o estado foi informado. */
	private static void validateState(String state) {
		if (state == null || state.isBlank()) {
			throw new IllegalArgumentException("O estado não pode ser nulo ou vazio.");
		}
	}
	
	public static void main(String[] args) {
		CultivarsDAO dao = new CultivarsDAO();
		/*Set<String> cultivarIds = new HashSet();
		cultivarIds.add(Constants.CONTO_NS + "cv_Itapua_202");
		cultivarIds.add(Constants.CONTO_NS + "cv_Suprema");
		cultivarIds.add(Constants.CONTO_NS + "cv_Brasilia");
		
		Map<String, List<Integer>> meses = dao.getMonthsToPlantByCultivars(cultivarIds, "RS");
		System.out.println(meses); */
		
		System.out.println(dao.getCultivar("cv_Itapua_202", "RS"));
	}
}
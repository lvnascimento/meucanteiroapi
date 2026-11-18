package br.edu.ifrs.hortafacil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.edu.ifrs.hortafacil.dao.CultivarsDAO;
import br.edu.ifrs.hortafacil.dao.ProductDAO;
import br.edu.ifrs.hortafacil.dao.TermDAO;
import br.edu.ifrs.hortafacil.dto.CultivarDTO;
import br.edu.ifrs.hortafacil.dto.CultivarPlantingSignatureDTO;
import br.edu.ifrs.hortafacil.model.cultivars.Cultivar;
import br.edu.ifrs.hortafacil.model.cultivars.CultivarID;
import br.edu.ifrs.hortafacil.model.general.Term;
import br.edu.ifrs.hortafacil.model.harvest.Harvest;
import br.edu.ifrs.hortafacil.model.harvest.HarvestProfile;
import br.edu.ifrs.hortafacil.model.management.Management;
import br.edu.ifrs.hortafacil.model.management.ManagementProfile;
import br.edu.ifrs.hortafacil.model.management.planting.CultivarPlantingProfile;
import br.edu.ifrs.hortafacil.model.management.planting.CultivarPlantingSignature;
import br.edu.ifrs.hortafacil.model.prefs.CultivarPreferenceProfile;
import br.edu.ifrs.hortafacil.model.prefs.CultivarPreferenceSignature;
import br.edu.ifrs.hortafacil.model.product.Product;
import br.edu.ifrs.hortafacil.model.product.ProductProfile;

/**
 * Controlador REST da API do Meu Canteiro.
 * <p>
 * Expõe consultas de produtos, recomendações por mês/estado, perfis de cultivo
 * de um produto, detalhes de uma cultivar e definições de termos.
 */
@RestController
@RequestMapping("/meucanteiroapi")
public class Controller {

	// -----------------------------------------------------------------------
	// Endpoints
	// -----------------------------------------------------------------------

	/**
	 * Recomenda produtos adequados para plantio em um mês e estado.
	 *
	 * @param month mês (1 a 12)
	 * @param state estado (UF)
	 */
	@GetMapping("/recommendations/{month}/{state}")
	public List<Product> recommendProducts(@PathVariable Integer month, @PathVariable String state) {
		ProductDAO dao = new ProductDAO();
		return dao.getProductsByMonthAndState(month, state);
	}

	/**
	 * Lista produtos. Sem o parâmetro {@code prodName} (ou com ele vazio),
	 * devolve todos; caso contrário, filtra pelo nome.
	 */
	@GetMapping("/products")
	public List<Product> getProducts(@RequestParam(required = false, defaultValue = "") String prodName) {
		ProductDAO dao = new ProductDAO();
		// Com defaultValue, prodName nunca é nulo; basta tratar vazio ou só espaços.
		if (prodName.trim().isEmpty())
			return dao.getAllProducts();
		return dao.getProductsByName(prodName);
	}

	/**
	 * Monta os perfis de cultivo de um produto para um mês e estado.
	 * <p>
	 * As cultivares do produto são agrupadas em quatro tipos de perfil, de modo que
	 * cultivares com as mesmas características compartilham um único perfil:
	 * preferências, plantio, manejo e colheita.
	 */
	@GetMapping("/products/{resource}/{month}/{state}")
	public ProductProfile getProductProfile(@PathVariable String resource, @PathVariable Integer month, @PathVariable String state) {
		CultivarsDAO dao = new CultivarsDAO();
		List<Cultivar> cultivars = dao.getCultivarsByProduct(resource, month, state);

		ProductProfile profile = new ProductProfile();
		profile.setPreferencesProfile(buildPreferencesProfiles(cultivars));
		profile.setPlantingProfile(buildPlantingProfiles(cultivars));
		profile.setManagementProfile(buildManagementProfiles(cultivars));
		profile.setHarvestProfile(buildHarvestProfiles(cultivars));
		return profile;
	}

	/**
	 * Devolve os detalhes de uma cultivar.
	 *
	 * @throws ResponseStatusException 404 se a cultivar não existir
	 */
	@GetMapping("/cultivars/{resource}/{state}")
	public CultivarDTO getCultivar(@PathVariable String resource, @PathVariable String state) {
		CultivarsDAO cDAO = new CultivarsDAO();
		Cultivar cultivar = cDAO.getCultivar(resource, state);
		if (cultivar == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cultivar não encontrada: " + resource);
		return new CultivarDTO(cultivar);
	}

	/** Devolve a definição de um termo a partir do seu rótulo. */
	@GetMapping("/terms/{label}")
	public Term getTermByLabel(@PathVariable String label) {
		TermDAO dao = new TermDAO();
		return dao.getTermByLabel(label);
	}

	// -----------------------------------------------------------------------
	// Construção dos perfis
	// -----------------------------------------------------------------------

	/** Agrupa as cultivares que têm exatamente as mesmas preferências de cultivo. */
	private static List<CultivarPreferenceProfile> buildPreferencesProfiles(List<Cultivar> cultivars) {
		Map<CultivarPreferenceSignature, Set<CultivarID>> bySignature =
				groupCultivarIdsBy(cultivars, Cultivar::getPreferences);

		List<CultivarPreferenceProfile> profiles = new ArrayList<>(bySignature.size());
		for (Entry<CultivarPreferenceSignature, Set<CultivarID>> e : bySignature.entrySet()) {
			profiles.add(new CultivarPreferenceProfile(e.getKey(), e.getValue()));
		}
		return profiles;
	}

	/**
	 * Agrupa as cultivares que têm exatamente as mesmas informações de plantio.
	 * Cultivares sem informações de plantio ficam de fora, pois não há assinatura
	 * a partir da qual montar o perfil.
	 */
	private static List<CultivarPlantingProfile> buildPlantingProfiles(List<Cultivar> cultivars) {
		Map<CultivarPlantingSignature, Set<CultivarID>> bySignature =
				groupCultivarIdsBy(cultivars, Cultivar::getPlantingInfo);

		List<CultivarPlantingProfile> profiles = new ArrayList<>(bySignature.size());
		for (Entry<CultivarPlantingSignature, Set<CultivarID>> e : bySignature.entrySet()) {
			if (e.getKey() == null)
				continue;
			profiles.add(new CultivarPlantingProfile(new CultivarPlantingSignatureDTO(e.getKey()), e.getValue()));
		}
		return profiles;
	}

	/**
	 * Agrupa os manejos: manejos aplicáveis exatamente ao mesmo conjunto de
	 * cultivares formam um único perfil.
	 */
	private static List<ManagementProfile> buildManagementProfiles(List<Cultivar> cultivars) {
		Map<Management, Set<CultivarID>> cultivarsByManagement =
				groupCultivarIdsByItem(cultivars, Cultivar::getManagement);
		Map<Set<CultivarID>, List<Management>> byCultivarSet =
				groupItemsByCultivarSet(cultivarsByManagement);

		List<ManagementProfile> profiles = new ArrayList<>(byCultivarSet.size());
		for (Entry<Set<CultivarID>, List<Management>> e : byCultivarSet.entrySet()) {
			profiles.add(new ManagementProfile(e.getValue(), e.getKey()));
		}
		return profiles;
	}

	/**
	 * Agrupa as colheitas: colheitas aplicáveis exatamente ao mesmo conjunto de
	 * cultivares formam um único perfil.
	 */
	private static List<HarvestProfile> buildHarvestProfiles(List<Cultivar> cultivars) {
		Map<Harvest, Set<CultivarID>> cultivarsByHarvest =
				groupCultivarIdsByItem(cultivars, Cultivar::getHarvest);
		Map<Set<CultivarID>, List<Harvest>> byCultivarSet =
				groupItemsByCultivarSet(cultivarsByHarvest);

		List<HarvestProfile> profiles = new ArrayList<>(byCultivarSet.size());
		for (Entry<Set<CultivarID>, List<Harvest>> e : byCultivarSet.entrySet()) {
			profiles.add(new HarvestProfile(e.getValue(), e.getKey()));
		}
		return profiles;
	}

	// -----------------------------------------------------------------------
	// Auxiliares de agrupamento
	// -----------------------------------------------------------------------
	// Usam LinkedHashMap/LinkedHashSet para manter a ordem de inserção. Com
	// HashMap/HashSet a ordem dos perfis podia mudar entre requisições, e como o
	// app nomeia os perfis pela posição (A, B, C...), o "Perfil A" podia trocar
	// de conteúdo. A ordem agora acompanha a das cultivares devolvidas pelo DAO.
	// O agrupamento depende de equals/hashCode das classes do modelo
	// (assinaturas, Management, Harvest e CultivarID).

	/**
	 * Agrupa os identificadores das cultivares por uma característica única
	 * (por exemplo, a assinatura de preferências).
	 *
	 * @param keyExtractor obtém a característica de cada cultivar
	 */
	private static <K> Map<K, Set<CultivarID>> groupCultivarIdsBy(List<Cultivar> cultivars, Function<Cultivar, K> keyExtractor) {
		Map<K, Set<CultivarID>> result = new LinkedHashMap<>();
		for (Cultivar c : cultivars) {
			result.computeIfAbsent(keyExtractor.apply(c), k -> new LinkedHashSet<>())
					.add(c.getDescription());
		}
		return result;
	}

	/**
	 * Agrupa os identificadores das cultivares por item de uma coleção
	 * (por exemplo, cada manejo ou colheita aplicável à cultivar).
	 * Cultivares sem a coleção (nula) são ignoradas.
	 *
	 * @param itemsExtractor obtém a coleção de itens de cada cultivar
	 */
	private static <T> Map<T, Set<CultivarID>> groupCultivarIdsByItem(List<Cultivar> cultivars, Function<Cultivar, ? extends Collection<T>> itemsExtractor) {
		Map<T, Set<CultivarID>> result = new LinkedHashMap<>();
		for (Cultivar c : cultivars) {
			Collection<T> items = itemsExtractor.apply(c);
			if (items == null)
				continue;
			for (T item : items) {
				result.computeIfAbsent(item, k -> new LinkedHashSet<>())
						.add(c.getDescription());
			}
		}
		return result;
	}

	/**
	 * Inverte o mapa "item -> cultivares": itens que se aplicam exatamente ao
	 * mesmo conjunto de cultivares ficam juntos na mesma lista.
	 */
	private static <T> Map<Set<CultivarID>, List<T>> groupItemsByCultivarSet(Map<T, Set<CultivarID>> cultivarsByItem) {
		Map<Set<CultivarID>, List<T>> result = new LinkedHashMap<>();
		for (Entry<T, Set<CultivarID>> e : cultivarsByItem.entrySet()) {
			result.computeIfAbsent(e.getValue(), k -> new ArrayList<>())
					.add(e.getKey());
		}
		return result;
	}
}
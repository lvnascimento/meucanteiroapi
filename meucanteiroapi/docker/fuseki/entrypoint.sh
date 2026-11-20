#!/bin/sh
set -e

DB=/data/horta

# Carrega os TTL só na primeira vez (ou se FORCE_RELOAD=true)
if [ ! -d "$DB" ] || [ "$FORCE_RELOAD" = "true" ]; then
  echo ">> Carregando arquivos TTL em $DB ..."
  rm -rf "$DB"
  java -cp /opt/fuseki/fuseki-server.jar tdb2.tdbloader --loc="$DB" /import/*.ttl \
    || { echo "Falha ao carregar os TTL"; rm -rf "$DB"; exit 1; }
fi

# Sem --update: o Fuseki só aceita consultas (mais seguro se a API apenas lê).
# Se a API precisar gravar, acrescente --update.
exec ./fuseki-server --tdb2 --loc="$DB" /horta
TARGETS="${1:-create-schema empty-schema import}"
pushd work/db/kfs-db/db-impex/impex

set -x
ant $TARGETS -Dimpex.properties.file=../../../../../impex-build-local-mysql.properties
set +x

popd
#!/bin/sh
DATE=`date "+%Y%m%d%H%M"`
COMMIT=`git rev-parse HEAD`
SHORTCOMMIT=`git rev-parse --short=8 HEAD`
REL_VERSION=`git describe --tags --long --dirty --always | sed s/oess-plugin-// | sed s/-/_/g`
SIMPLE_VERSION=`git describe --tags | sed s/oess-plugin-//`

cp na2-oess-plugin.spec.tmpl na2-oess-plugin.spec

sed -i -e "s;@@DATE@@;${DATE};" na2-oess-plugin.spec
sed -i -e "s;@@COMMIT@@;${COMMIT};" na2-oess-plugin.spec
sed -i -e "s;@@VERSION@@;${REL_VERSION};" na2-oess-plugin.spec
sed -i -e "s;@@SIMPLE_VERSION@@;${SIMPLE_VERSION};" na2-oess-plugin.spec


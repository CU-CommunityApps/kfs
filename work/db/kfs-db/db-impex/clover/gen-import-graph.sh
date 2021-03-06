#
# Copyright 2012 The Kuali Foundation
# 
# Licensed under the Educational Community License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# 
# http://www.opensource.org/licenses/ecl2.php
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# DO NOT add comments before the blank line below, or they will disappear.

export DB=$1
export SCHEMA=`echo $2 | tr '[:lower:]' '[:upper:]'`
export TABLE=`echo $3 | tr '[:lower:]' '[:upper:]'`
export FILE=`echo $3 | tr '[:upper:]' '[:lower:]'`_import
export TRUNCATE=$4
ant -emacs gen-import-graph -Ddatabase.config=$DB -Dtable.schema=$SCHEMA -Dtable.name=$TABLE -Dconfig.file=$FILE -Dtable.truncate=$TRUNCATE

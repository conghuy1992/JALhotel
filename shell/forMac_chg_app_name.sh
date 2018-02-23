#! /bin/bash

if [ $# -ne 1 ]; then
  echo "指定された引数は$#個です。" 1>&2
  echo "実行するには1個の引数が必要です。" 1>&2
  exit 1
fi

#プロジェクトのルートディレクトリに移動
cd ../

#アプリ名を置換
dir=app/res/values
file=strings.xml
LANG=C
NOLOCALE=1
find $dir -name "${file}" | xargs sed -i "" "s/<string name=\"app_name\">.*<\/string>/<string name=\"app_name\">${1}<\/string>/"

dir=app/res/values-ja
file=strings.xml
LANG=C
NOLOCALE=1
find $dir -name "${file}" | xargs sed -i "" "s/<string name=\"app_name\">.*<\/string>/<string name=\"app_name\">${1}<\/string>/"
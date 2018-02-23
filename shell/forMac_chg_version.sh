#! /bin/bash

if [ $# -ne 1 ]; then
  echo "指定された引数は$#個です。" 1>&2
  echo "実行するには1個の引数が必要です。" 1>&2
  exit 1
fi

#プロジェクトのルートディレクトリに移動
cd ../

dir=app
file=AndroidManifest.xml

#バージョンコードを取得してインクリメント
result=`cat ${dir}/${file} | grep versionCode="\(.*\)"`
code=$(expr "$result" : '.*"\([0-9]*\)"')
code=$(expr $code + 1)

#バージョンコードとバージョン名を置換
LANG=C
NOLOCALE=1
find $dir -name "${file}" -maxdepth 1 | xargs sed -i "" "s/android:versionCode=\".*\"/android:versionCode=\"${code}\"/"
find $dir -name "${file}" -maxdepth 1 | xargs sed -i "" "s/android:versionName=\".*\"/android:versionName=\"${1}\"/"



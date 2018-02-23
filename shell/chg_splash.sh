#! /bin/bash

if [ $# -ne 1 ]; then
  echo "指定された引数は$#個です。" 1>&2
  echo "実行するには1個の引数が必要です。" 1>&2
  exit 1
fi

#プロジェクトのルートディレクトリに移動
cd ../

#アプリ名を置換
dir=app/src/com/thebrand_app/master/brand_preview
file=PreviewManager.java

LANG=C
NOLOCALE=1
find $dir -name "${file}" | xargs sed -i "s/public static final String SPLASH_NAME_P = \"40\";/public static final String SPLASH_NAME_P = \"40_${1}\";/"

find $dir -name "${file}" | xargs sed -i "s/public static final String SPLASH_NAME_L = \"41\";/public static final String SPLASH_NAME_L = \"41_${1}\";/"
#! /bin/bash

if [ $# -ne 1 ]; then
  echo "指定された引数は$#個です。" 1>&2
  echo "実行するには1個の引数が必要です。" 1>&2
  exit 1
fi

#プロジェクトのルートディレクトリに移動
cd ../

#MainApplication.javaを基準にjavaファイルのパスを取得
src_dir=app/src
root_file=MainApplication.java
root_file_path=$(find $src_dir -name $root_file)
echo "root_file_path = $root_file_path"

#old_src_pathが取れなかったら異常終了
if [ -z "$root_file_path" ]; then
  echo "failed to find java source code"
  exit 1
fi

#現在のソースコードへのパス
old_src_path=$(dirname $root_file_path)

#old_src_pathが取れなかったら異常終了
if [ -z "$old_src_path" ]; then
  echo "failed to get old_src_path"
  exit 1
fi
    echo "old_src_path = $old_src_path"


#現在のパッケージ名を文字列と配列で取得
old_pkg_dir=${old_src_path##app/src/}
old_pkg_name=(`echo $old_pkg_dir | tr -s '/' '.'`)
old_pkg_array=(`echo $old_pkg_dir | tr -s '/' ' '`)

    i=0
    for e in ${old_pkg_array[@]}; do
        echo "old_pkg_array[$i] = ${e}"
        let i++
    done

#中間生成パッケージ名
inner_pkg_name="f91ddc064c9c385d"

#新しいパッケージ名を文字列と配列で取得
new_pkg_name=$1
new_pkg_array=(`echo $1 | tr -s '.' ' '`)

    i=0
    for e in ${new_pkg_array[@]}; do
        echo "new_pkg_array[$i] = ${e}"
        let i++
    done

#パッケージ名が同じだったら何もしない
if [ $old_pkg_name = $new_pkg_name ]; then
  echo "same package name"
  exit 1
fi

#新しいソースコードのパス
new_src_path=( `echo $new_pkg_name | tr -s '.' '/'`)
new_src_path=$src_dir/$new_src_path
    echo "new_src_path = $new_src_path"

#新旧パッケージ名で一致している階層を計算
i=0
for e in ${new_pkg_array[@]}; do
	if [ "$e" != "${old_pkg_array[i]}" ]; then
	  unmatch=$i
	  break
	fi
	let i++
done
      echo "unmatch = $unmatch"

#新ディレクトリを作成
mk_dir=$src_dir
i=0
for e in ${new_pkg_array[@]}; do
	mk_dir=$mk_dir/${e}
	if [ $i -ge $unmatch ]; then
    	mkdir $mk_dir
    fi
	let i++
done

#ソースコードをコピー
cp -R ${old_src_path}/* ${new_src_path}/

#旧ディレクトリを削除
rm_dir=$src_dir
i=0
for e in ${old_pkg_array[@]}; do
    rm_dir=$rm_dir/$e
    if [ $i -eq $unmatch ]; then
        break
    fi
    let i++
done
    echo "rm_dir = $rm_dir"

rm -r $rm_dir

#パッケージ名を置換
LANG=C
NOLOCALE=1
find app \( -name "*.java" -o -name "*.xml" \) | xargs sed -i "" "s/${old_pkg_name}/${inner_pkg_name}/"
find app \( -name "*.java" -o -name "*.xml" -o -name "proguard-project.txt" \) | xargs sed -i "" "s/${old_pkg_name}/${inner_pkg_name}/"

find app \( -name "*.java" -o -name "*.xml" \) | xargs sed -i "" "s/${inner_pkg_name}/${new_pkg_name}/"
find app \( -name "*.java" -o -name "*.xml" -o -name "proguard-project.txt" \) | xargs sed -i "" "s/${inner_pkg_name}/${new_pkg_name}/"

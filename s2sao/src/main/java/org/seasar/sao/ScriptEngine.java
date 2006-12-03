/*
 * Copyright 2004-2006 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.sao;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * スクリプト操作を行うエンジン。具体的なスクリプト処理系毎に実装を用意し、
 * S2SaoInterceptorにDIする。
 * @author Masataka Kurihara (Gluegent,Inc.)
 */
public interface ScriptEngine extends Serializable {

	/**
	 * SAOクラス型から、対応するスクリプトファイルを検索し、コンパイルする。
	 * スクリプトファイルが見つからなかった場合、nullを返す。
	 * @param sao 処理対象SAOのクラス型。 
	 * @return 対応するスクリプトファイルのコンパイル結果。
	 * @throws Exception スクリプトファイルのコンパイル時例外。
	 */
	Object compile(Class<?> sao) throws Throwable;

	/**
	 * コンパイル済みスクリプト中に、実行すべきスクリプトメソッドが含まれているか
	 * どうかをテストする。
	 * @param compiled コンパイル済みスクリプト。
	 * @param method 実行中のSAOメソッド。
	 * @return 実行すべきスクリプトメソッドが見つかれば、trueを返す。
	 */
	boolean hasFunction(Object compiled, Method method);

	/**
	 * スクリプトメソッドを実行する。
	 * @param compiled コンパイル済みスクリプト。
	 * @param method 実行中のSAOメソッド。
	 * @param args 実行中のSAOメソッドに渡された引数。
	 * @param expectedClass 返りに期待される型。
	 * @return Java型に変換済みのスクリプトメソッド実行結果。
	 * @throws Exception スクリプトメソッドの実行時例外。
	 */
	Object invoke(Object compiled, Method method, Object[] args,
			Class<?> expectedClass) throws Throwable;
	
}

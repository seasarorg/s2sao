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
 * �X�N���v�g������s���G���W���B��̓I�ȃX�N���v�g�����n���Ɏ�����p�ӂ��A
 * S2SaoInterceptor��DI����B
 * @author Masataka Kurihara (Gluegent,Inc.)
 */
public interface ScriptEngine extends Serializable {

	/**
	 * SAO�N���X�^����A�Ή�����X�N���v�g�t�@�C�����������A�R���p�C������B
	 * �X�N���v�g�t�@�C����������Ȃ������ꍇ�Anull��Ԃ��B
	 * @param sao �����Ώ�SAO�̃N���X�^�B 
	 * @return �Ή�����X�N���v�g�t�@�C���̃R���p�C�����ʁB
	 * @throws Exception �X�N���v�g�t�@�C���̃R���p�C������O�B
	 */
	Object compile(Class<?> sao) throws Throwable;

	/**
	 * �R���p�C���ς݃X�N���v�g���ɁA���s���ׂ��X�N���v�g���\�b�h���܂܂�Ă��邩
	 * �ǂ������e�X�g����B
	 * @param compiled �R���p�C���ς݃X�N���v�g�B
	 * @param method ���s����SAO���\�b�h�B
	 * @return ���s���ׂ��X�N���v�g���\�b�h��������΁Atrue��Ԃ��B
	 */
	boolean hasFunction(Object compiled, Method method);

	/**
	 * �X�N���v�g���\�b�h�����s����B
	 * @param compiled �R���p�C���ς݃X�N���v�g�B
	 * @param method ���s����SAO���\�b�h�B
	 * @param args ���s����SAO���\�b�h�ɓn���ꂽ�����B
	 * @param expectedClass �Ԃ�Ɋ��҂����^�B
	 * @return Java�^�ɕϊ��ς݂̃X�N���v�g���\�b�h���s���ʁB
	 * @throws Exception �X�N���v�g���\�b�h�̎��s����O�B
	 */
	Object invoke(Object compiled, Method method, Object[] args,
			Class<?> expectedClass) throws Throwable;
	
}

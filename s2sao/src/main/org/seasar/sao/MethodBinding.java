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

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * SAO���̃��\�b�h���Ή�����A�X�N���v�g���\�b�h���w�肷��B
 * ���̎w����s��Ȃ��ƁA�����̃��\�b�h���擾�����B
 * @author Masataka Kurihara (Gluegent,Inc.)
 */
@Retention(RUNTIME)
@Target({ METHOD })
public @interface MethodBinding {

	/**
	 * @return �X�N���v�g���\�b�h���B
	 */
	String value() default "";

}

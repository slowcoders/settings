package org.slowcoders.io.serialize;

import java.lang.annotation.RetentionPolicy;

/**
 * Created by zeedh on 02/02/2018.
 */

@java.lang.annotation.Retention(RetentionPolicy.RUNTIME)
public @interface IOCtrl {
	
	String key() default "";

	Class<IOAdapter> adapter() default IOAdapter.class;

	int flags() default 0;

}

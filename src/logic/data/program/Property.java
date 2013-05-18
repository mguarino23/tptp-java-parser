/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package logic.data.program;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Property {
    public String[] dname();
}

/*
Copyright 2006, 2007, 2008, 2009 Hao Xu
xuh@cs.unc.edu

This file is part of OSHL-S.

OSHL-S is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

OSHL-S is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Foobar; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package logic.data.program;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import logic.data.fol.FunctionSymbol;

public class ProgramParametersDyn implements ProgramParameters {

    public static String fileName = null;
    public static boolean hasConjecture = false;
    private static boolean enableCandidateCache = false;
    private static int candidateCacheSize = CACHE_SIZE;
    private static int mruCacheSize = MRU_CACHE_SIZE;
    private static int bs = 1;
    private static boolean statistics = false;
    private static boolean initial;
    public static int timeout = TIMEOUT;
    public static boolean typeInference = TYPE_INFERENCE;
    public static boolean equality = false;
    public static boolean verbose = false;
    public static boolean boundedTries = true;
    private static boolean enableAdjustBatchSize = false;
    private static boolean enableFrequencySorting = true;
    private static boolean groundLevel = true;
    private static boolean enableTIC = true;
    @Property(dname={"trie indexed cache", "tic"})
    public static synchronized boolean isEnableTrieIndexedCache() {
        return enableTIC;
    }

    public static synchronized void setEnableTrieIndexedCache(boolean groundLevel) {
        ProgramParametersDyn.enableTIC = groundLevel;
    }

    @Property(dname={"ground"})
    public static synchronized boolean isGroundLevel() {
        return groundLevel;
    }

    public static synchronized void setGroundLevel(boolean groundLevel) {
        ProgramParametersDyn.groundLevel = groundLevel;
    }
    private static HashMap<String, Method> methodMap = new HashMap<String, Method>();

    public static boolean setParameter(String name, String param) {
        if (methodMap.isEmpty()) {
            String sname;
            String gname;
            Method[] mets = ProgramParametersDyn.class.getMethods();
            for (Method met : mets) {
                if ((met.getModifiers() & Modifier.STATIC) != 0) {
                    Property a = met.getAnnotation(Property.class);
                    if (a != null) {
                            sname = null;
                            gname = met.getName();
                            if (gname.startsWith("is")) {
                                sname = "set" + gname.substring(2);
                            } else if (gname.startsWith("get")) {
                                sname = "set" + gname.substring(3);
                            }
                            Class rtype = met.getReturnType();
                            try {
                            final Method declaredMethod = ProgramParametersDyn.class.getDeclaredMethod(sname, rtype);
                            for (String n : a.dname()) {
                                    methodMap.put(n, declaredMethod);
                            }
                            } catch (NoSuchMethodException ex) {
                                Logger.getLogger(ProgramParametersDyn.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (SecurityException ex) {
                                Logger.getLogger(ProgramParametersDyn.class.getName()).log(Level.SEVERE, null, ex);
                            }
                    }
                }
            }
        }
        Method m = methodMap.get(name);
        if (m == null) {
            return false;
        }
        Class rtype = m.getParameterTypes()[0];
        if (rtype.equals(Boolean.TYPE) || rtype.equals(Integer.TYPE) || rtype.equals(String.class)) {
            try {
                Object parameter = rtype.equals(Boolean.TYPE) ? Boolean.parseBoolean(param)
                        : rtype.equals(Integer.TYPE) ? Integer.parseInt(param) : param;
                m.invoke(null, parameter);
                return true;
            } catch (Exception ex) {
                Logger.getLogger(ProgramParametersDyn.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    @Property(dname = {"frequency sorting", "fs"})
    public synchronized static boolean isEnableFrequencySorting() {
        return enableFrequencySorting;
    }

    public synchronized static void setEnableFrequencySorting(boolean enableFrequencySorting) {
        ProgramParametersDyn.enableFrequencySorting = enableFrequencySorting;
    }
    public static boolean computeMaxLength = true;
    private static String TPTPPath = null;

    @Property(dname = {"tptp path", "tptp"})
    public synchronized static String getTPTPPath() {
        return TPTPPath;
    }

    public synchronized static void setTPTPPath(String TPTPPath) {
        ProgramParametersDyn.TPTPPath = TPTPPath;
    }

    @Property(dname = {"max length", "maxlength", "ml"})
    public static boolean isComputeMaxLength() {
        return computeMaxLength;
    }

    public static void setComputeMaxLength(boolean computeMaxLength) {
        ProgramParametersDyn.computeMaxLength = computeMaxLength;
    }
    public static SortedSet<FunctionSymbol> fs = new TreeSet<FunctionSymbol>();

    @Property(dname = {"bounded tries", "bt"})
    public synchronized static boolean isEnableBoundedTries() {
        return ProgramParametersDyn.boundedTries;
    }

    public synchronized static void setEnableBoundedTries(boolean bbt) {
        ProgramParametersDyn.boundedTries = bbt;
    }
    //private static int diff;

    @Property(dname = {"statistics", "stat"})
    public synchronized static boolean isStatistics() {
        return statistics;
    }

    public synchronized static void setStatistics(boolean statistics) {
        ProgramParametersDyn.statistics = statistics;
    }

    @Property(dname = {"candidate cache size", "cachesize"})
    public synchronized static int getCandidateCacheSize() {
        return candidateCacheSize;
    }

    public synchronized static void setCandidateCacheSize(int size) {
        candidateCacheSize = size;
    }

    @Property(dname = {"MRU cache size", "mrucachesize", "cs"})
    public synchronized static int getMRUCacheSize() {
        return mruCacheSize;
    }

    public synchronized static void setMRUCacheSize(int size) {
        mruCacheSize = size;
    }

    public synchronized static boolean isHasConjecture() {
        return hasConjecture;
    }

    public synchronized static void setHasConjecture(boolean hasConjecture) {
        ProgramParametersDyn.hasConjecture = hasConjecture;
    }

    @Property(dname = {"candidate cache", "cache"})
    public synchronized static boolean isEnableCandidateCache() {
        return enableCandidateCache;
    }

    public synchronized static void setEnableCandidateCache(boolean ecc) {
        enableCandidateCache = ecc;
    }

    @Property(dname = {"file name", "input"})
    public synchronized static String getFileName() {
        return fileName;
    }

    public synchronized static void setFileName(String fileNameParam) {
        fileName = fileNameParam;
    }

    @Property(dname = {"verbose"})
    public synchronized static boolean isVerbose() {
        return verbose;
    }

    @Property(dname = {"initial"})
    public synchronized static boolean getInitialInterpretation() {
        return initial;
    }

    public synchronized static void setInitialInterpretation(boolean parseBoolean) {
        initial = parseBoolean;
    }

    public synchronized static void setVerbose(boolean verbose) {
        ProgramParametersDyn.verbose = verbose;
    }

    @Property(dname = {"batch size", "batchsize"})
    public synchronized static int getBatchSize() {
        return bs;
    }

    public synchronized static void setBatchSize(int batchSize) {
        ProgramParametersDyn.bs = batchSize;
    }

    @Property(dname = {"adjust batch size", "bs"})
    public synchronized static boolean isEnableAdjustBatchSize() {
        return enableAdjustBatchSize;
    }

    public synchronized static void setEnableAdjustBatchSize(boolean enableAdjustBatchSize) {
        ProgramParametersDyn.enableAdjustBatchSize = enableAdjustBatchSize;
    }

    public synchronized static boolean isEquality() {
        return equality;
    }

    public synchronized static void setEquality(boolean equality) {
        ProgramParametersDyn.equality = equality;
    }

    @Property(dname = {"timeout", "to"})
    public synchronized static int getTimeout() {
        return timeout;
    }

    public synchronized static void setTimeout(int timeout) {
        ProgramParametersDyn.timeout = timeout;
    }

    public synchronized static void setTypeInference(boolean typeInference) {
        ProgramParametersDyn.typeInference = typeInference;
    }

    @Property(dname = {"type inference", "typeinference", "ti"})
    public synchronized static boolean getTypeInference() {
        return typeInference;
    }

    public static String toDisplayString() {
        StringBuffer buf;
        try {
            buf = new StringBuffer();
            for (Method met : ProgramParametersDyn.class.getDeclaredMethods()) {
                if ((met.getModifiers() & Modifier.STATIC) != 0) {
                    Annotation[] as = met.getAnnotations();
                    if (as.length > 0 && as[0] instanceof Property) {
                        Property p = (Property) as[0];
                        String[] pn = p.dname();
                        buf.append(pn[0]);
                        buf.append("=");
                        buf.append(met.invoke(null));
                        buf.append("\n");
                    }
                }
            }
            return buf.toString();//"initial = " + getInitialInterpretation() + ", timeout = " + getTIMEOUT() + ", type_inference = " + getTYPE_INFERENCE() + ", candidate_cache = " + isEnableCandidateCache() + ", candidate_cache_size = " + getCandidateCacheSize() + ", batch_size = " + getBatchSize() + ", verbose = " + isVerbose() + ", statistics = " + isStatistics();
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ProgramParametersDyn.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ProgramParametersDyn.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(ProgramParametersDyn.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "error";
    }

    public static void addFunctionSymbol(FunctionSymbol fs) {
        ProgramParametersDyn.fs.add(fs);
    }

    @Property(dname = {"rank"})
    public synchronized static boolean isRank() {
        return rank;
    }
    private static boolean rank = true;

    public synchronized static void setRank(boolean r) {
        rank = r;
    }
}

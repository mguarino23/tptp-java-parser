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
package logic.function.syntax;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.Stack;

public class TokenStream {

    public static final int TYPE_STRING = '\"';
    Stack<Token> las = new Stack<Token>();
    StreamTokenizer tk2;
    Token t;

    public class Token {

        String string;
        int ttype;

        public Token(String string, int ttype) {
            super();
            this.string = string;
            this.ttype = ttype;
        }
    }

    public TokenStream(String string) {
        this(new StringReader(string));
    }

    public boolean moreTokens() {
        boolean notEmpty = !las.isEmpty();
        return notEmpty || tk2.ttype != StreamTokenizer.TT_EOF;

    }

    public int tokenType() {
        return t.ttype;
    }

    public void push() {
        las.push(t);

    }

    @Deprecated
    public void push(String o) {
        push();
    }

    public void skip(String string) throws IOException {
        int i = 0;

        while (moreTokens() && string.equals(nextToken())) {
            i++;
        }
        //System.out.println(string + ":" + i);
        push();
    }

    public void skipOnce(String string) throws TokenStreamException {
        try {
            String token;
            if (!(token = nextToken()).equals(string)) {
                throw new TokenStreamException("\"" + string + "\" expected, but was \"" + token + "\"");
            }
        } catch (IOException e) {
            throw new TokenStreamException(e);
        }
    }

    public TokenStream(Reader rd) {
        this.tk2 = new StreamTokenizer(rd);
        tk2.eolIsSignificant(false);
        tk2.quoteChar('\"');
        tk2.ordinaryChar(';');
        tk2.ordinaryChar('[');
        tk2.ordinaryChar(']');
        tk2.ordinaryChar(',');
        tk2.ordinaryChar('@');
        tk2.ordinaryChar('/');
        tk2.wordChars('_', '_');//edited
        tk2.commentChar('%');//edited
    }

    public String nextToken() throws IOException {
        if (las.isEmpty()) {
            tk2.nextToken();
            String string = null;
            switch (tk2.ttype) {
                case StreamTokenizer.TT_WORD:
                    string = tk2.sval;
                    break;
                case StreamTokenizer.TT_NUMBER:
                    string = Integer.toString((int) tk2.nval);
                    break;
                case '\"':
                    string = tk2.sval;
                    break;
                //edited
                case '\'':
                    string = tk2.sval;
                    break;
                //edited
                case '!':
                    char ch = (char) tk2.ttype;
                    tk2.nextToken();
                    if (tk2.ttype == '=') {
                        string = ch + "=";
                    } else {
                        tk2.pushBack();
                        string = "" + ch;
                    }
                    break;
                case '=':
                case '-':
                    ch = (char) tk2.ttype;
                    tk2.nextToken();
                    if (tk2.ttype == '>') {
                        string = ch + ">";
                    } else {
                        tk2.pushBack();
                        string = "" + ch;
                    }
                    break;
                case '<':
                    // support for tptp connectives
                    ch = (char) tk2.ttype;
                    tk2.nextToken();
                    if (tk2.ttype == '=' || tk2.ttype == '~') {
                        string = "" + ch + (char) tk2.ttype;
                        tk2.nextToken();
                        if (tk2.ttype == '>') {
                            string += ">";
                        } else {
                            tk2.pushBack();
                        }
                    } else {
                        tk2.pushBack();
                        string = "" + ch;
                    }
                    break;
                case '~':
                    // support for tptp connectives
                    ch = (char) tk2.ttype;
                    tk2.nextToken();
                    if (tk2.ttype == '|' || tk2.ttype == '&' || tk2.ttype =='>') {
                        string = "" + ch + (char) tk2.ttype;
                    } else {
                        tk2.pushBack();
                        string = "" + ch;
                    }
                    break;
                case ':':

                    tk2.nextToken();
                    if (tk2.ttype == ':') {
                        string = "::";
                    } else {
                        tk2.pushBack();
                        string = ":";
                    }
                    break;
                default:
                    string = String.valueOf((char) tk2.ttype);
            }
            t = new Token(string, tk2.ttype);
//            System.out.println("Next token: "+string);
            return string;
        } else {
            t = las.pop();
            return t.string;
        }
    }
}

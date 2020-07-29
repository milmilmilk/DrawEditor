
/**
 * String で表された式の値を計算するクラス
 *
 * 参考文献 Java 再帰下降構文解析 超入門
 * https://qiita.com/7shi/items/64261a67081d49f941e3
 */
public class CalculateString {

    private final String str;
    private int pos;

    protected double paramT;

    public CalculateString(String str, double t) {
        this.str = str;
        paramT = t;
    }

    public final int peek() {
        if (pos < str.length()) {
            return str.charAt(pos);
        }
        return -1;
    }

    public final void next() {
        ++pos;
    }
}

/**
 * String で表された式の値を計算するクラス
 *
 * 参考文献 Java 再帰下降構文解析 超入門
 * https://qiita.com/7shi/items/64261a67081d49f941e3
 */
class Parser extends CalculateString {

    public Parser(String str, double t) {
        super(str, t);
    }

    /**
     * 数
     * @return 値
     */
    public final double number() {
        StringBuilder sb = new StringBuilder();
        int ch;
        if(peek()=='t') {
        		return paramT;
        }else {
	        while ((ch = peek()) >= 0 && (Character.isDigit(ch) || ch=='.')) {
	            sb.append((char) ch);
	            next();
	        }
	        return Double.parseDouble(sb.toString());
        }
    }

    /**
     * 式
     * @return 式の値
     */
    // expr = term, {("+", term) | ("-", term)}
    public final double expr() {
        double x = term();
        for (;;) {
            switch (peek()) {
                case '+':
                    next();
                    x += term();
                    continue;
                case '-':
                    next();
                    x -= term();
                    continue;
            }
            break;
        }
        return x;
    }

    // term = factor, {("*", factor) | ("/", factor)}
    /**
     * 項
     * @return 項の値
     */
    public final double term() {
        double x = factor();
        for (;;) {
            switch (peek()) {
                case '*':
                    next();
                    x *= factor();
                    continue;
                case '/':
                    next();
                    x /= factor();
                    continue;
            }
            break;
        }
        return x;
    }

    // factor = factor = [spaces], ("(", expr, ")") | number, [spaces]
    public final double factor() {
        double ret;
        spaces();
        if (peek() == '(') {
            next();
            ret = expr();
            if (peek() == ')') {
                next();
            }
        } else {
            ret = number();
        }
        spaces();
        return ret;
    }

    public void spaces() {
        while (peek() == ' ') {
            next();
        }
    }
}

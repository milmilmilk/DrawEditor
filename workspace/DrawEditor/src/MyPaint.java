
import java.awt.*;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.geom.*;
import java.io.Serializable;
import java.util.*;

/**
 * LinarGradientPaint と Color を格納する．グラデーションの情報に対して Serializable を実装するためのクラス．
 */
public class MyPaint implements Serializable{

	/**
	 * グラデーションを使用するかどうか
	 */
	private boolean useGradation;

	/**
	 * 色
	 */
	private Color color;

	/**
	 * LinarGradientPaint で使用する座標を 0.0 ~ 1.0 で指定する．
	 */
	private double startX, startY, endX, endY;
	/**
	 * 色が変わる位置を 0.0 ~ 1.0 で指定する．
	 */
	private ArrayList<Float> fractions;
	/**
	 * グラデーションの，場所における色のリスト
	 */
	private ArrayList<Color> colors;
	/**
	 * グラデーションの繰り返しの種類
	 */
	private CycleMethod cycleMethod;

	/**
	 * 引数を指定して，新しい MyPaint を作成する．
	 * @param lgp グラデーション
	 * @param c 色
	 * @param gradation グラデーションを使用するかどうか
	 */
	public MyPaint(LinearGradientPaint lgp, Color c, boolean gradation) {
		useGradation = gradation;

		setLinearGradientPaint(lgp);
		setColor(c);
	}

	/**
	 * 引数と同じ MyPaint を新しく作成する．
	 * @param p コピー元の MyPaint
	 */
	public MyPaint(MyPaint p) {
		setLinearGradientPaint(p.getLinearGradientPaint(0.0, 0.0, 1.0, 1.0));
		setColor(p.getColor());
		useGradation = p.getUseGradation();
	}

	/**
	 * LinearGradientPaint を指定して，MyPaint の変数に格納する．
	 * @param lgp 線形グラデーション
	 */
	public void setLinearGradientPaint(LinearGradientPaint lgp) {

		if(lgp == null) {
			useGradation = false;
			return;
		}

		Point2D start = lgp.getStartPoint();
		Point2D end = lgp.getEndPoint();
		startX = start.getX();
		startY = start.getY();
		endX = end.getX();
		endY = end.getY();
		float f[] = lgp.getFractions();
		Color c[] = lgp.getColors();
		fractions = new ArrayList<Float>(f.length);
		colors = new ArrayList<Color>(c.length);
		for(int i=0; i<f.length; i++) {
			fractions.add(f[i]);
			colors.add(c[i]);
		}
		cycleMethod = lgp.getCycleMethod();
	}

	public void setColor(Color c) {
		if(c == null) {
			color = null;
		}else {
			color = new Color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
		}
	}

	/**
	 * 指定された引数の位置に表示するための LinearGradientPaint を返す．
	 * @param x 表示する x 座標
	 * @param y 表示する y 座標
	 * @param width 表示する幅
	 * @param height 表示する高さ
	 * @return グラデーション
	 */
	public LinearGradientPaint getLinearGradientPaint(double x, double y, double width, double height) {
		if(fractions == null || colors == null) return null;
		double w = width==0? 1: width;
		double h = height==0? 1: height;
		float sx = (float)(startX * w + x);
		float sy = (float)(startY * h + y);
		float ex = (float)(endX * w + x);
		float ey = (float)(endY * h + y);
		float[] f = new float[fractions.size()];
		Color[] c = new Color[colors.size()];
		for(int i=0; i<f.length; i++) {
			f[i] = fractions.get(i);
			c[i] = colors.get(i);
		}
		return new LinearGradientPaint(sx, sy, ex, ey, f, c, (cycleMethod==null? CycleMethod.NO_CYCLE: cycleMethod));
	}

	public Color getColor() {
		return color;
	}

	/**
	 * 指定された引数の位置に表示するための Paint を返します．単色またはグラデーションを返す．
	 * @param x 表示する x 座標
	 * @param y 表示する y 座標
	 * @param width 表示する幅
	 * @param height 表示する高さ
	 * @return 単色またはグラデーション
	 */
	public Paint getPaint(double x, double y, double width, double height) {
		if(useGradation) {
			return getLinearGradientPaint(x, y, width, height);
		}else {
			return getColor();
		}
	}

	/**
	 * 指定された引数の位置に表示するための Paint を返す．単色またはグラデーションを返す．
	 * @param rect 表示する長方形
	 * @return 単色またはグラデーション
	 */
	public Paint getPaint(Rectangle2D rect) {
		return getPaint(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
	}

	public boolean getUseGradation() {
		return useGradation;
	}

}

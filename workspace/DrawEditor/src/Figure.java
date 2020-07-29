
import java.awt.*;
import java.awt.geom.*;
import java.io.*;
import java.util.*;

/**
 * 描画する形を扱うクラス．このクラスを継承して図形を定義することによって ViewPane に描画することができる．
 */
class Figure implements Serializable{

	public final transient static int FIGURE = 0;
	public final transient static int RECTANGLE_FIGUE = 1;
	public final transient static int OVAL_FIGUE = 2;
	public final transient static int TERMINATOR_FIGUE = 3;
	public final transient static int DOCUMENT_FIGUE = 4;
	public final transient static int DECISION_FIGUE = 5;
	public final transient static int DATA_FIGUE = 6;
	public final transient static int DATABASE_FIGUE = 7;
	public final transient static int PREDEFINED_FIGUE = 8;
	public final transient static int LOOPSTART_FIGUE = 9;
	public final transient static int LOOPEND_FIGUE = 10;
	public final transient static int OFFPAGE_FIGUE = 11;
	public final transient static int PREPARATION_FIGUE = 12;
	public final transient static int INPUT_FIGUE = 13;
	public final transient static int DISPLAY_FIGUE = 14;
	public final transient static int L_FIGUE = 15;
	public final transient static int LINE_FIGUE = 16;
	public final transient static int FREE_FIGUE = 17;
	public final transient static int ARW_FIGUE = 18;
	public final transient static int ARROW1_FIGUE = 19;
	public final transient static int ARROW2_FIGUE = 20;
	public final transient static int ARROW3_FIGUE = 21;
	public final transient static int ARROW4_FIGUE = 22;
	public final transient static int STRING_FIGUE = 23;
	public final transient static int IMAGE_FIGUE = 24;
	public final transient static int GROUP_FIGUE = 25;

	public int type;

	protected transient DrawModel model;

	/**
	 * 位置
	 */
	protected double x, y;
	/**
	 * 大きさ
	 */
	protected double width, height;
	/**
	 * その時点の shape を囲う長方形
	 */
	protected transient Rectangle2D bounds;
	/**
	 * 線の太さ
	 */
	protected double lineWidth;
	/**
	 * その時点の形
	 */
	protected transient Shape shape;
	/**
	 * 色に関する情報
	 */
	protected MyPaint paint;
	/**
	 * 複製や復元をするために，これまで shape に施した変形を記録しておく変数
	 */
	protected AffineTransform affine;
	/**
	 * 描画時に適応される変換
	 */
	protected transient AffineTransform drawingAffine;

	/**
	 * 空の Figure を作成する．
	 */
	public Figure(){
		type = FIGURE;
		x = y = width = height = 0;
		lineWidth = 1;
		shape = new Rectangle2D.Double(x, y, width, height);
		paint = null;
		affine = new AffineTransform();
		drawingAffine = new AffineTransform();
	}

	/**
	 * 色を指定して Figure を作成する．
	 * @param x x 座標
	 * @param y y 座標
	 * @param w 幅
	 * @param h 高さ
	 * @param c 色
	 * @param l 線の太さ
	 * @param model DrawModel
	 */
	public Figure(double x, double y, double w, double h, Color c, double l, DrawModel model) {
		type = FIGURE;
		this.x = x;
		this.y = y; // this.x, this.y はインスタンス変数を指します．
		width = w;
		height = h; // ローカル変数で同名の変数がある場合は，this
		lineWidth = l;
		paint = new MyPaint(null, c, false);
		this.model = model;
		affine = new AffineTransform();
		drawingAffine = new AffineTransform();
		bounds = new Rectangle2D.Double(x, y, w, h);
	}

	/**
	 * グラデーションを指定して Figure を作成する．
	 * @param x x 座標
	 * @param y y 座標
	 * @param w 幅
	 * @param h 高さ
	 * @param p グラデーション
	 * @param l 線の太さ
	 * @param model DrawModel
	 */
	public Figure(double x, double y, double w, double h, LinearGradientPaint p, double l, DrawModel model) {
		type = FIGURE;
		this.x = x;
		this.y = y; // this.x, this.y はインスタンス変数を指します．
		width = w;
		height = h; // ローカル変数で同名の変数がある場合は，this
		lineWidth = l;
		paint = new MyPaint(p, null, true);
		this.model = model;
		affine = new AffineTransform();
		drawingAffine = new AffineTransform();
		bounds = new Rectangle2D.Double(x, y, w, h);
	}

	/**
	 * 初期化された Figure を指定して，新しい Figure を作成する．
	 * @param f 初期化された Figure
	 */
	public Figure(Figure f) {
		type = FIGURE;
		this.x = f.x;
		this.y = f.y;
		this.width = f.width;
		this.height = f.height;
		this.lineWidth = f.lineWidth;
		this.shape = null;
		this.paint = new MyPaint(f.paint);
		this.model = f.model;
		this.affine = (AffineTransform) f.affine.clone();
		drawingAffine = new AffineTransform();
		bounds = f.bounds==null? null: (Rectangle2D.Double)f.bounds.clone();
	}

	/**
	 * f をこの Figure にディープコピーする．
	 * @param f コピーする Figure
	 */
	public void deepCopy(Figure f) {
		type = f.type;
		this.x = f.x;
		this.y = f.y;
		this.width = f.width;
		this.height = f.height;
		this.lineWidth = f.lineWidth;
		this.shape = null;
		this.paint = new MyPaint(f.paint);
		this.model = f.model;
		this.affine = (AffineTransform) f.affine.clone();
		drawingAffine = new AffineTransform();
		bounds = f.bounds==null? null: (Rectangle2D.Double) f.bounds.clone();
	}

	/**
	 * affine を恒等変換で初期化する．
	 */
	public void initAffine() {
		affine = new AffineTransform();
	}

	/**
	 * この Figure のタイプを返す．
	 * @return タイプ
	 */
	public int getType() {
		return type;
	}

	/**
	 * サイズを指定する．
	 * @param w 幅
	 * @param h 高さ
	 */
	public void setSize(int w, int h) {
		width = w;
		height = h;
	}

	/**
	 * この Figure の位置座標を指定する．
	 * @param x x 座標
	 * @param y y 座標
	 */
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Drawing の最中に形が変わったら呼び出される．
	 * @param x1 始点の x 座標
	 * @param y1 始点の y 座標
	 * @param x2 終点の x 座標
	 * @param y2 終点の y 座標
	 */
	public void reshape(int x1, int y1, int x2, int y2) {
		int newx = Math.min(x1, x2);
		int newy = Math.min(y1, y2);
		int neww = Math.abs(x1 - x2);
		int newh = Math.abs(y1 - y2);
		setLocation(newx, newy);
		setSize(neww, newh);
		setBounds();
	}

	/**
	 * shape を作成する． Drawing の最中に呼び出される．
	 */
	public void makeShape() {
	}

	/**
	 * Drawing が終わった時に呼び出される
	 * @param x1 始点の x 座標
	 * @param y1 始点の y 座標
	 * @param x2 終点の x 座標
	 * @param y2 終点の y 座標
	 */
	public void complete(int x1, int y1, int x2, int y2) {

	}

	public MyPaint getMyPaint() {
		return paint;
	}

	/**
	 * GroupFigureであるかどうか
	 * @return Figure のリスト
	 */
	public ArrayList<Figure> getFigures() {
		return null; // GroupFigure でないことを示す．
	}

	/**
	 * 点が bounds 内にあるかどうか判定する．
	 * @param mx 点の x 座標
	 * @param my 点の y 座標
	 * @return 点が bounds 内にあるかどうか．
	 */
	public boolean isInternal(double mx, double my){
		Rectangle2D r = bounds; // = this.getBounds();
		return r.contains(mx, my);
	}

	/**
	 * Figure の変換モードを判定する．
	 * @param mx マウスの x 座標
	 * @param my マウスの y 座標
	 * @return 変換モード
	 */
	public int editMode(double mx, double my) {
		Rectangle2D r = bounds; //= this.getBounds();
		int mark = DrawModel.MARK_SIZE;

		// スケーリング
		int x1 = (int)r.getX();
		int y1 = (int)r.getY();
		int x2 = (int)(r.getX() + r.getWidth()/2 - mark/2);
		int y2 = (int)(r.getY() + r.getHeight()/2 - mark/2);
		int x3 = (int)(r.getX() + r.getWidth() - mark);
		int y3 = (int)(r.getY() + r.getHeight() - mark);
		if((new Rectangle(x3, y3, mark, mark)).contains(mx, my)) return 1;
		if((new Rectangle(x3, y2, mark, mark)).contains(mx, my)) return 2;
		if((new Rectangle(x2, y3, mark, mark)).contains(mx, my)) return 3;
		if((new Rectangle(x2, y1, mark, mark)).contains(mx, my)) return 4;
		if((new Rectangle(x1, y2, mark, mark)).contains(mx, my)) return 5;
		if((new Rectangle(x1, y1, mark, mark)).contains(mx, my)) return 6;
		if((new Rectangle(x3, y1, mark, mark)).contains(mx, my)) return 7;
		if((new Rectangle(x1, y3, mark, mark)).contains(mx, my)) return 8;

		// 回転移動
		if((new Rectangle(x2, y1+mark, mark, mark)).contains(mx, my)) return 9;

		// シャーリング
		if((new Rectangle(x3, y2-mark, mark, mark)).contains(mx, my)) return 10;
		if((new Rectangle(x2-mark, y3, mark, mark)).contains(mx, my)) return 11;
		if((new Rectangle(x1, y2+mark, mark, mark)).contains(mx, my)) return 12;
		if((new Rectangle(x2+mark, y1, mark, mark)).contains(mx, my)) return 13;

		// 平行移動
		if(isInternal(mx, my)) return -1;

		// 恒等変換
		return 0;
	}

	/**
	 * drawingAffine を指定する．
	 * @param afn drawingAffine
	 */
	public void setDrawingAffine(AffineTransform afn) {
		drawingAffine = (AffineTransform) afn.clone();
	}

	/**
	 * drawingAffine を生成する．
	 * @param mode 変換モード
	 * @param sx 始点の x 座標
	 * @param sy 始点の y 座標
	 * @param ex 終点の x 座標
	 * @param ey 終点の y 座標
	 */
	public void setDrawingAffine(int mode, double sx, double sy, double ex, double ey) {
		AffineTransform afn = new AffineTransform();
		double x = bounds.getX();
		double y = bounds.getY();
		double w = bounds.getWidth();
		double h = bounds.getHeight();

		double scaleX, scaleY;

		switch(mode) {

		case -1:
			afn.translate(ex-sx, ey-sy);
			break;

		case 0:
			break;

		case 1:
			scaleX = (ex-x)/(sx-x);
			scaleY = (ey-y)/(sy-y);
			afn.translate(x, y);
			afn.scale(scaleX, scaleY);
			afn.translate(-x, -y);
			break;

		case 2:
			scaleX = (ex-x)/(sx-x);
			afn.translate(x, 0);
			afn.scale(scaleX, 1);
			afn.translate(-x, -0);
			break;

		case 3:
			scaleY = (ey-y)/(sy-y);
			afn.translate(0, y);
			afn.scale(1, scaleY);
			afn.translate(-0, -y);
			break;

		case 4:
			scaleY = (h + sy - ey)/(h);
			afn.translate(0, y+h);
			afn.scale(1, scaleY);
			afn.translate(-0, -y-h);
			break;

		case 5:
			scaleX = (w + sx - ex)/(w);
			afn.translate(x+w, 0);
			afn.scale(scaleX, 1);
			afn.translate(-x-w, -0);
			break;

		case 6:
			scaleX = (w + sx - ex)/(w);
			scaleY = (h + sy - ey)/(h);
			afn.translate(x+w, y+h);
			afn.scale(scaleX, scaleY);
			afn.translate(-x-w, -y-h);
			break;

		case 7:
			scaleX = (ex-x)/(sx-x);
			scaleY = (h + sy - ey)/(h);
			afn.translate(x, y+h);
			afn.scale(scaleX, scaleY);
			afn.translate(-x, -y-h);
			break;

		case 8:
			scaleX = (w + sx - ex)/(w);
			scaleY = (ey-y)/(sy-y);
			afn.translate(x+w, y);
			afn.scale(scaleX, scaleY);
			afn.translate(-x-w, -y);
			break;

		case 9:
			double ox = x+w/2;
			double oy = y+h/2;
			double theta = culcArg(ex, ey, ox, oy) - culcArg(sx, sy, ox, oy);
			afn.translate(ox, oy);
			afn.rotate(theta);
			afn.translate(-ox, -oy);
			break;

		case 10:
			afn.translate(x, 0);
			afn.shear(0, (ey-sy)/w);
			afn.translate(-x, -0);
			break;

		case 11:
			afn.translate(0, y);
			afn.shear((ex-sx)/h, 0);
			afn.translate(0, -y);
			break;

		case 12:
			afn.translate(x+w, 0);
			afn.shear(0, (sy-ey)/w);
			afn.translate(-x-w, -0);
			break;

		case 13:
			afn.translate(0, y+h);
			afn.shear((sx-ex)/h, 0);
			afn.translate(0, -y-h);
			break;

		default:
			afn = null;
			break;
		}
		drawingAffine = afn;
	}

	/**
	 * 角度を計算する．
	 * @param ex 角度を知りたい点の x 座標
	 * @param ey 角度を知りたい点の y 座標
	 * @param ox 中心の x 座標
	 * @param oy 中心の y 座標
	 * @return 角度
	 */
	private double culcArg(double ex, double ey, double ox, double oy) {
			if (ex == ox) return (ey>=oy)? Math.PI/2: -Math.PI/2;
			double atan =  Math.atan((double)(ey - oy)/(ex - ox));
			return (ex < ox)? (atan + Math.PI): atan;
	}


	public AffineTransform getDrawingAffine() {
		return drawingAffine;
	}

	public AffineTransform getAffine() {
		return affine;
	}

	/**
	 * 変換が終了したら， drawingAffine の変換を shape に施し， affine に記録する．
	 */
	public void completeTransform() {
		shape = drawingAffine.createTransformedShape(shape);
		resetTransform();
		setBounds();
	}

	/**
	 * completeTransform 内で，各図形で共通する操作を実行する．
	 */
	public void resetTransform() {
		affine = synthesis(affine, drawingAffine);
		drawingAffine = new AffineTransform();
	}

	/**
	 * AffineTransform によって定義される変換を合成する．
	 * @param ta 変換A
	 * @param tb 変換B
	 * @return 合成された変換(BA)
	 */
	public static AffineTransform synthesis(AffineTransform ta, AffineTransform tb) {
		double[] m = new double[6];
		double[] a = new double[6];
		double[] b = new double[6];
		ta.getMatrix(a);
		tb.getMatrix(b);

		m[0] = b[0]*a[0] + b[2]*a[1];
		m[1] = b[1]*a[0] + b[3]*a[1];
		m[2] = b[0]*a[2] + b[2]*a[3];
		m[3] = b[1]*a[2] + b[3]*a[3];
		m[4] = b[0]*a[4] + b[2]*a[5] + b[4];
		m[5] = b[1]*a[4] + b[3]*a[5] + b[5];

		return new AffineTransform(m);

	}

	/**
	 * bounds を返す．必ず setBounds() の後に呼ぶ．
	 * @return bounds
	 */
	public Rectangle2D getBounds() {
		return bounds;// shape.getBounds2D();
	}

	/**
	 * この Figure を囲う長方形を bounds に格納する．
	 */
	public void setBounds() {
		bounds = shape.getBounds2D();
	}

	/**
	 * 描画
	 * @param g グラフィックス
	 */
	public void draw(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setPaint(paint.getPaint(bounds));
		if (lineWidth >= 0) {
			g2.setStroke(new BasicStroke((float) lineWidth));
			g2.draw(drawingAffine.createTransformedShape(shape));
		} else {
			g2.fill(drawingAffine.createTransformedShape(shape));
		}
	}
}

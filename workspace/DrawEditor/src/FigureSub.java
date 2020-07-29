import java.awt.*;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.*;
import java.io.*;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * 長方形
 */
class RectangleFigure extends Figure {

	// 引数付きのコンストラクタは継承されないので，コンストラクタを定義
	public RectangleFigure(Figure f) {
		super(f);
		type = Figure.RECTANGLE_FIGUE;
		makeShape();
	}

	@Override
	public void reshape(int x1, int y1, int x2, int y2) {
		super.reshape(x1, y1, x2, y2);
		makeShape();
	}

	@Override
	public void makeShape() {
		shape = new Rectangle2D.Double(x, y, width, height);
	}
}

/**
 * 楕円
 */
class OvalFigure extends Figure {

	public OvalFigure(Figure f) {
		super(f);
		type = Figure.OVAL_FIGUE;
		makeShape();
	}

	@Override
	public void reshape(int x1, int y1, int x2, int y2) {
		super.reshape(x1, y1, x2, y2);
		makeShape();
	}

	@Override
	public void makeShape() {
		shape = new Ellipse2D.Double(x, y, width, height);
	}
}

/**
 * 端子
 */
class TerminatorFigure extends Figure {

	public TerminatorFigure(Figure f) {
		super(f);
		type = Figure.TERMINATOR_FIGUE;
		makeShape();
	}

	@Override
	public void reshape(int x1, int y1, int x2, int y2) {
		super.reshape(x1, y1, x2, y2);
		makeShape();
	}

	@Override
	public void makeShape() {
		double arc = Math.min(width, height);
		shape = new RoundRectangle2D.Double(x, y, width, height, arc, arc);
	}
}

/**
 * 文章
 */
class DocumentFigure extends Figure {

	// 弧の曲がり具合を決める直角三角形
	private final transient static double BASE = 3.0 / 12.0; // (1/4)
	private final transient static double SIDE = 4.0 / 12.0;
	private final transient static double HYPOTENUSE = 5.0 / 12.0;

	public DocumentFigure(Figure f) {
		super(f);
		type = Figure.DOCUMENT_FIGUE;
		makeShape();
	}

	@Override
	public void reshape(int x1, int y1, int x2, int y2) {
		super.reshape(x1, y1, x2, y2);
		makeShape();
	}

	@Override
	public void makeShape() {
		Area size = new Area(new Rectangle2D.Double(x, y, width, height));
		Area rect = new Area(new Rectangle2D.Double(x, y, width, height - (width * (HYPOTENUSE - SIDE))));
		Area downward = new Area(new Ellipse2D.Double(x - width * (HYPOTENUSE - BASE),
				y + height - width * (2 * HYPOTENUSE), width * 2 * HYPOTENUSE, width * 2 * HYPOTENUSE));
		Area upward = new Area(new Ellipse2D.Double(x + width * (1 - BASE - HYPOTENUSE),
				y + height - width * 2 * (SIDE - BASE), width * 2 * HYPOTENUSE, width * 2 * HYPOTENUSE));
		rect.add(downward);
		rect.subtract(upward);
		rect.intersect(size);
		shape = rect;
	}
}

/**
 * 条件
 */
class DecisionFigure extends Figure {

	public DecisionFigure(Figure f) {
		super(f);
		type = Figure.DECISION_FIGUE;
		makeShape();
	}

	@Override
	public void reshape(int x1, int y1, int x2, int y2) {
		super.reshape(x1, y1, x2, y2);
		makeShape();
	}

	@Override
	public void makeShape() {
		int xPoints[] = { (int) (x + width / 2), (int) (x), (int) (x + width / 2), (int) (x + width) };
		int yPoints[] = { (int) (y), (int) (y + height / 2), (int) (y + height), (int) (y + height / 2) };
		shape = new Polygon(xPoints, yPoints, 4);
	}
}

/**
 * 入出力
 */
class DataFigure extends Figure {

	private final transient static double BASE = 1.0 / 5.0;

	public DataFigure(Figure f) {
		super(f);
		type = Figure.DATA_FIGUE;
		makeShape();
	}

	@Override
	public void reshape(int x1, int y1, int x2, int y2) {
		super.reshape(x1, y1, x2, y2);
		makeShape();
	}

	@Override
	public void makeShape() {
		int xPoints[] = { (int) (x + width * BASE), (int) (x), (int) (x + width * (1 - BASE)), (int) (x + width) };
		int yPoints[] = { (int) (y), (int) (y + height), (int) (y + height), (int) (y) };
		shape = new Polygon(xPoints, yPoints, 4);
	}
}

/**
 * データベース
 */
class DatabaseFigure extends Figure {

	private final transient static double ELLIPSE_MINOR_AXIS = 1.0 / 8.0;
	private static double GAP = 0.01; // px

	public DatabaseFigure(Figure f) {
		super(f);
		type = Figure.DATABASE_FIGUE;
		GAP = lineWidth * 0.01;
		makeShape();
	}

	@Override
	public void reshape(int x1, int y1, int x2, int y2) {
		super.reshape(x1, y1, x2, y2);
		makeShape();
	}

	@Override
	public void makeShape() {
		//Area size = new Area(new Rectangle2D.Double(x, y, width, height));
		Area rect = new Area(new Rectangle2D.Double(x, y + width * ELLIPSE_MINOR_AXIS, width,
				height - width * (2 * ELLIPSE_MINOR_AXIS)));
		Area bottom = new Area(new Ellipse2D.Double(x, y + height - width * (2 * ELLIPSE_MINOR_AXIS), width,
				width * 2 * ELLIPSE_MINOR_AXIS));
		Area top = new Area(new Ellipse2D.Double(x, y, width, width * 2 * ELLIPSE_MINOR_AXIS));
		Area top2 = new Area(
				new Ellipse2D.Double(x - GAP, y - GAP, width + 2 * GAP, width * 2 * ELLIPSE_MINOR_AXIS + 2 * GAP));
		rect.add(bottom);
		rect.add(top);
		top2.subtract(top);
		rect.subtract(top2);
		shape = rect;
	}
}

/**
 * 定義済み処理
 */
class PredefinedFigure extends Figure {

	private final transient static double BASE = 1.0 / 6.0;
	private static double GAP = 0.001; // px

	public PredefinedFigure(Figure f) {
		super(f);
		type = Figure.PREDEFINED_FIGUE;
		GAP = lineWidth * 0.01;
		makeShape();
	}

	@Override
	public void reshape(int x1, int y1, int x2, int y2) {
		super.reshape(x1, y1, x2, y2);
		makeShape();
	}

	@Override
	public void makeShape() {
		Area size = new Area(new Rectangle2D.Double(x, y, width, height));
		Area rect = new Area(new Rectangle2D.Double(x + width * BASE, y, width - 2 * width * BASE, height));
		Area rect2 = new Area(new Rectangle2D.Double(x + width * BASE - GAP, y - GAP,
				width - 2 * width * BASE + 2 * GAP, height + 2 * GAP));
		rect2.subtract(rect);
		size.subtract(rect2);
		shape = size;
	}
}

/**
 * 繰り返し開始
 */
class LoopstartFigure extends Figure {

	private final transient static double SIDE = 1.0 / 3.0;
	private final transient static double BASE = 1.0 / 4.0;

	public LoopstartFigure(Figure f) {
		super(f);
		type = Figure.LOOPSTART_FIGUE;
		makeShape();
	}

	@Override
	public void reshape(int x1, int y1, int x2, int y2) {
		super.reshape(x1, y1, x2, y2);
		makeShape();
	}

	@Override
	public void makeShape() {
		double side = (height == 0) ? 0 : (width * BASE > height * SIDE) ? SIDE : width * BASE / height;
		int xPoints[] = { (int) (x + height * side), (int) (x), (int) (x), (int) (x + width), (int) (x + width),
				(int) (x + width - height * side) };
		int yPoints[] = { (int) (y), (int) (y + height * side), (int) (y + height), (int) (y + height),
				(int) (y + height * side), (int) (y) };
		shape = new Polygon(xPoints, yPoints, 6);
	}
}

/**
 * 繰り返し終了
 */
class LoopendFigure extends Figure {

	private final transient static double SIDE = 1.0 / 3.0;
	private final transient static double BASE = 1.0 / 4.0;

	public LoopendFigure(Figure f) {
		super(f);
		type = Figure.LOOPEND_FIGUE;
		makeShape();
	}

	@Override
	public void reshape(int x1, int y1, int x2, int y2) {
		super.reshape(x1, y1, x2, y2);
		makeShape();
	}

	@Override
	public void makeShape() {
		double side = (height == 0) ? 0 : (width * BASE > height * SIDE) ? SIDE : width * BASE / height;
		int xPoints[] = { (int) (x), (int) (x), (int) (x + height * side), (int) (x + width - height * side),
				(int) (x + width), (int) (x + width) };
		int yPoints[] = { (int) (y), (int) (y + height * (1 - side)), (int) (y + height), (int) (y + height),
				(int) (y + height * (1 - side)), (int) (y) };
		shape = new Polygon(xPoints, yPoints, 6);
	}
}


/**
 * 外部結合子
 */
class OffpageFigure extends Figure {

	private final transient static double SIDE = 1.0;
	private final transient static double BASE = 1.0 / 2.0;

	public OffpageFigure(Figure f) {
		super(f);
		type = Figure.OFFPAGE_FIGUE;
		makeShape();
	}

	@Override
	public void reshape(int x1, int y1, int x2, int y2) {
		super.reshape(x1, y1, x2, y2);
		makeShape();
	}

	@Override
	public void makeShape() {
		double side = (height == 0) ? 0 : (width * BASE > height * SIDE) ? SIDE : width * BASE / height;
		int xPoints[] = { (int) (x), (int) (x), (int) (x + height * side), (int) (x + width - height * side),
				(int) (x + width), (int) (x + width) };
		int yPoints[] = { (int) (y), (int) (y + height * (1 - side)), (int) (y + height), (int) (y + height),
				(int) (y + height * (1 - side)), (int) (y) };
		shape = new Polygon(xPoints, yPoints, 6);
	}
}

/**
 * 準備
 */
class PreparationFigure extends Figure {

	public PreparationFigure(Figure f) {
		super(f);
		type = Figure.PREPARATION_FIGUE;
		makeShape();
	}

	@Override
	public void reshape(int x1, int y1, int x2, int y2) {
		super.reshape(x1, y1, x2, y2);
		makeShape();
	}

	@Override
	public void makeShape() {
		if (width > height) {
			double side = height / 2;
			int xPoints[] = { (int) (x + side), (int) (x), (int) (x + side), (int) (x + width - side),
					(int) (x + width), (int) (x + width - side) };
			int yPoints[] = { (int) (y), (int) (y + side), (int) (y + height), (int) (y + height), (int) (y + side),
					(int) (y) };
			shape = new Polygon(xPoints, yPoints, 6);
		} else {
			double side = width / 2;
			int xPoints[] = { (int) (x + side), (int) (x), (int) (x), (int) (x + side), (int) (x + width),
					(int) (x + width) };
			int yPoints[] = { (int) (y), (int) (y + side), (int) (y + height - side), (int) (y + height),
					(int) (y + height - side), (int) (y + side) };
			shape = new Polygon(xPoints, yPoints, 6);
		}
	}
}

/**
 * 入力
 */
class InputFigure extends Figure {

	private final transient static double SIDE = 1.0 / 3.0;

	public InputFigure(Figure f) {
		super(f);
		type = Figure.INPUT_FIGUE;
		makeShape();
	}

	@Override
	public void reshape(int x1, int y1, int x2, int y2) {
		super.reshape(x1, y1, x2, y2);
		makeShape();
	}

	@Override
	public void makeShape() {
		int xPoints[] = { (int) (x), (int) (x), (int) (x + width), (int) (x + width) };
		int yPoints[] = { (int) (y + height * SIDE), (int) (y + height), (int) (y + height), (int) (y) };
		shape = new Polygon(xPoints, yPoints, 4);
	}
}

/**
 * 表示
 */
class DisplayFigure extends Figure {

	public DisplayFigure(Figure f) {
		super(f);
		type = Figure.DISPLAY_FIGUE;
		makeShape();
	}

	@Override
	public void reshape(int x1, int y1, int x2, int y2) {
		super.reshape(x1, y1, x2, y2);
		makeShape();
	}

	@Override
	public void makeShape() {
		double sq3 = Math.sqrt(3);
		Area rect = new Area(new Rectangle2D.Double(x + height * sq3 / 2, y, width - height, height));
		Area rect2 = new Area(new Rectangle2D.Double(x, y, height * (sq3 / 2) + 1, height));
		Area rect3 = new Area(
				new Rectangle2D.Double(x + width - height * (1 - sq3 / 2) - 1, y, height * (1 - sq3 / 2) + 1, height));
		Area bottom = new Area(new Ellipse2D.Double(x - height * (1 - sq3 / 2), y - height, height * 2, height * 2));
		Area top = new Area(new Ellipse2D.Double(x - height * (1 - sq3 / 2), y, height * 2, height * 2));
		Area tail = new Area(new Ellipse2D.Double(x + width - height * 2, y - height / 2, height * 2, height * 2));
		bottom.intersect(top);
		bottom.intersect(rect2);
		tail.intersect(rect3);
		rect.add(bottom);
		rect.add(tail);
		shape = rect;
	}
}

/**
 * 線に関係するクラスに継承して使うためのクラス．
 */
class LFigure extends Figure {

	protected double sx, sy, ex, ey;
	protected double lw; // abs(lineWidth)

	public LFigure(Figure f) {
		super(f);
		type = Figure.L_FIGUE;
		sx = f.x;
		sy = f.y;
		ex = f.x + f.width;
		ey = f.y + f.height;
		lw = Math.abs(lineWidth);
	}

	@Override
	public void deepCopy(Figure f) {
		super.deepCopy(f);
		LFigure f2 = (LFigure) f;
		this.sx = f2.sx;
		this.sy = f2.sy;
		this.ex = f2.ex;
		this.ey = f2.ey;
		this.lw = f2.lw;
	}

	/**
	 * 線の始点と終点を指定する．
	 * @param sx 始点の x 座標
	 * @param sy 始点の y 座標
	 * @param ex 終点の x 座標
	 * @param ey 終点の y 座標
	 */
	public void setLine(double sx, double sy, double ex, double ey) {
		this.sx = sx;
		this.sy = sy;
		this.ex = ex;
		this.ey = ey;
	}

	@Override
	public void reshape(int x1, int y1, int x2, int y2) {
		super.reshape(x1, y1, x2, y2);
		setLine(x1, y1, x2, y2);
	}

	public void setBounds() {
		super.setBounds();
		if(bounds.getWidth() < 1) bounds = new Rectangle2D.Double(bounds.getX(), bounds.getY(), 1, bounds.getHeight());
		if(bounds.getHeight() < 1) bounds = new Rectangle2D.Double(bounds.getX(), bounds.getY(), bounds.getWidth(), 1);
	}

	@Override
	public void draw(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setPaint(paint.getPaint(bounds));
		g2.setStroke(new BasicStroke((float) lw));
		g2.draw(drawingAffine.createTransformedShape(shape));

	}

}

/**
 * 線
 */
class LineFigure extends LFigure {

	public LineFigure(Figure f) {
		super(f);
		type = Figure.LINE_FIGUE;
		makeShape();
	}

	@Override
	public void reshape(int x1, int y1, int x2, int y2) {
		super.reshape(x1, y1, x2, y2);
		makeShape();
	}

	@Override
	public void makeShape() {
		shape = new Line2D.Double(sx, sy, ex, ey);
	}
}

/**
 * フリーハンド
 */
class FreeFigure extends LFigure {

	protected ArrayList<Point2D> via;

	public FreeFigure(Figure f) {
		super(f);
		type = Figure.FREE_FIGUE;
		via = new ArrayList<Point2D>();
		via.add(new java.awt.geom.Point2D.Double(x, y));
		makeShapeDrawing();
	}

	@Override
	public void deepCopy(Figure f) {
		super.deepCopy(f);
		FreeFigure f2 = (FreeFigure) f;
		this.via = new ArrayList<Point2D>();
		for(int i=0; i<f2.via.size(); i++) {
			this.via.add((Point2D)f2.via.get(i).clone());
		}
	}

	@Override
	public void reshape(int x1, int y1, int x2, int y2) {
		if(x2 < x) {
			width += x - x2;
			x = x2;
		}
		if(y2 < y) {
			height += y - y2;
			y = y2;
		}
		if(x2 > x + width) {
			width = x2 - x;
		}
		if(y2 > y + height) {
			height = y2 - y;
		}
		super.setLine(x1, y1, x2, y2);
		makeShapeDrawing();
	}

	@Override
	public void makeShape() {
	}

	/**
	 * Drawing の最中に，本来 makeShpe が呼び出されるところで呼び出される． makeShape はクローンを作る時に呼ばれる．
	 */
	public void makeShapeDrawing() {
		via.add(new Point2D.Double(ex, ey));
	}

	@Override
	public void complete(int x1, int y1, int x2, int y2) {
		super.complete(x1, y1, x2, y2);
		setBounds();
	}

	@Override
	public void completeTransform() {
		for(int i=0; i<via.size(); i++) {
			via.set(i, drawingAffine.transform(via.get(i), null));
		}
		drawingAffine = new AffineTransform();
		// resetTransform(); を実行しない．viaが記録できるので，affineを記録する必要がないため．
		setBounds();
	}

	@Override
	public void setBounds() {
		double bx = via.get(0).getX();
		double by = via.get(0).getY();
		double cx = bx;
		double cy = by;
		for(int i=1; i<via.size(); i++) {
			double vx = via.get(i).getX();
			double vy = via.get(i).getY();
			if(vx < bx) {
				bx = vx;
			}
			if(vy < by) {
				by = vy;
			}
			if(vx > cx) {
				cx = vx;
			}
			if(vy > cy) {
				cy = vy;
			}
		}
		bounds = new Rectangle2D.Double(bx, by, cx-bx, cy-by);
	}

	@Override
	public void draw(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setPaint(paint.getPaint(bounds));
		g2.setStroke(new BasicStroke((float) lw, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		for (int i = 0; i < via.size() - 1; i++) {
			Line2D line = new Line2D.Double(via.get(i), via.get(i+1));
			g2.draw(drawingAffine.createTransformedShape(line));
		}
	}

}

/**
 * 矢印に関係するクラスに継承して使うためのクラス．矢印の先端の三角形の座標が計算される．
 */
class ArwFigure extends LFigure {

	private static final transient double TOP_SIZE = 10.0; // px
	private static final transient double TOP_BASE = 5.0; // px;

	protected double tx1, ty1, tx2, ty2; // 矢印の先端の座標 // tx0, ty0
	protected double bx, by; // 矢印の先端の根元の座標

	protected transient ArrayList<Shape> shapes;
	protected transient ArrayList<Stroke> strokes;

	public ArwFigure(Figure f) {
		super(f);
		type = Figure.ARW_FIGUE;
		shape = null;
		shapes = new ArrayList<Shape>();
		strokes = new ArrayList<Stroke>();
		culcTops();
	}

	@Override
	public void deepCopy(Figure f){
		super.deepCopy(f);
		ArwFigure f2 = (ArwFigure) f;
		this.tx1 = f2.tx1;
		this.ty1 = f2.ty1;
		this.tx2 = f2.tx2;
		this.ty2 = f2.ty2;
		this.bx = f2.bx;
		this.by = f2.by;
	}

	@Override
	public void reshape(int x1, int y1, int x2, int y2) {
		super.reshape(x1, y1, x2, y2);
		culcTops();
	}

	/**
	 * sx, sy, ex, ey から，tx1, ty1, tx2, ty2 を計算する．
	 */
	public void culcTops() {
		double dx = ex - sx;
		double dy = ey - sy;
		double hypot = Math.hypot(dx, dy);
		if (hypot == 0) {
			bx = tx1 = tx2 = sx;
			by = ty1 = ty2 = sy;
		} else {
			double tx = dy * (TOP_BASE+lineWidth) / hypot;
			double ty = -dx * (TOP_BASE+lineWidth) / hypot;
			bx = ex - dx * (TOP_SIZE+lineWidth) / hypot;
			by = ey - dy * (TOP_SIZE+lineWidth) / hypot;
			tx1 = bx - tx;
			ty1 = by - ty;
			tx2 = bx + tx;
			ty2 = by + ty;
		}
	}

	@Override
	public void completeTransform() {
		for(int i=0; i<shapes.size(); i++) {
			shapes.set(i,  drawingAffine.createTransformedShape(shapes.get(i))) ;
		}
		resetTransform();
		setBounds();
	}

	@Override
	public void setBounds() {
		Rectangle2D r = shapes.get(0).getBounds2D();
		double x1 = r.getX();
		double y1 = r.getY();
		double x2 = x1 + r.getWidth();
		double y2 = y1 + r.getHeight();
		for(int i=1; i<shapes.size(); i++) {
			r = shapes.get(i).getBounds2D();
			if(r.getX() < x1) x1 = r.getX();
			if(r.getY() < y1) y1 = r.getY();
			if(r.getX() + r.getWidth() > x2) x2 = r.getX() + r.getWidth();
			if(r.getY() + r.getHeight() > y2) y2 = r.getY() + r.getHeight();
		}
		bounds = new Rectangle2D.Double(x1, y1, x2-x1, y2-y1);
	}

	@Override
	public void draw(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setPaint(paint.getPaint(bounds));
		for (int i = 0; i < shapes.size(); i++) {
			if (strokes.get(i) != null) {
				g2.setStroke(strokes.get(i));
				g2.draw(drawingAffine.createTransformedShape(shapes.get(i)));
			} else {
				g2.fill(drawingAffine.createTransformedShape(shapes.get(i)));
			}
		}

	}
}

/**
 * 矢印 1
 */
class Arrow1Figure extends ArwFigure {

	public Arrow1Figure(Figure f) {
		super(f);
		type = Figure.ARROW1_FIGUE;
		makeShape();
	}

	@Override
	public void reshape(int x1, int y1, int x2, int y2) {
		super.reshape(x1, y1, x2, y2);
		makeShape();
	}

	@Override
	public void makeShape() {

		shapes = new ArrayList<Shape>();
		strokes = new ArrayList<Stroke>();

		strokes.add(new BasicStroke((float) lw));
		shapes.add(new Line2D.Double(sx, sy, ex, ey));

		strokes.add(new BasicStroke((float) lw));
		shapes.add(new Line2D.Double(ex, ey, tx1, ty1));

		strokes.add(new BasicStroke((float) lw));
		shapes.add(new Line2D.Double(ex, ey, tx2, ty2));
	}
}

/**
 * 矢印 2
 */
class Arrow2Figure extends ArwFigure {

	public Arrow2Figure(Figure f) {
		super(f);
		type = Figure.ARROW2_FIGUE;
		makeShape();
	}

	@Override
	public void reshape(int x1, int y1, int x2, int y2) {
		super.reshape(x1, y1, x2, y2);
		makeShape();
	}

	@Override
	public void makeShape() {

		shapes = new ArrayList<Shape>();
		strokes = new ArrayList<Stroke>();

		strokes.add(new BasicStroke((float) lw));
		shapes.add(new Line2D.Double(sx, sy, bx, by));

		strokes.add(new BasicStroke((float) lw));
		int xPoints[] = { (int) (ex), (int) (tx1), (int) (tx2) };
		int yPoints[] = { (int) (ey), (int) (ty1), (int) (ty2) };
		shapes.add(new Polygon(xPoints, yPoints, 3));
	}
}

/**
 * 矢印 3
 */
class Arrow3Figure extends ArwFigure {

	private static final transient float DASH = 4.0f;

	public Arrow3Figure(Figure f) {
		super(f);
		type = Figure.ARROW3_FIGUE;
		makeShape();
	}

	@Override
	public void reshape(int x1, int y1, int x2, int y2) {
		super.reshape(x1, y1, x2, y2);
		makeShape();
	}

	@Override
	public void makeShape() {

		shapes = new ArrayList<Shape>();
		strokes = new ArrayList<Stroke>();

		float dash[] = { DASH, DASH };
		strokes.add(new BasicStroke((float) lw, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
		shapes.add(new Line2D.Double(sx, sy, bx, by));

		strokes.add(new BasicStroke((float) lw));
		int xPoints[] = { (int) (ex), (int) (tx1), (int) (tx2) };
		int yPoints[] = { (int) (ey), (int) (ty1), (int) (ty2) };
		shapes.add(new Polygon(xPoints, yPoints, 3));
	}
}

/**
 * 矢印 4
 */
class Arrow4Figure extends ArwFigure {

	public Arrow4Figure(Figure f) {
		super(f);
		type = Figure.ARROW4_FIGUE;
		makeShape();
	}

	@Override
	public void reshape(int x1, int y1, int x2, int y2) {
		super.reshape(x1, y1, x2, y2);
		makeShape();
	}

	@Override
	public void makeShape() {

		shapes = new ArrayList<Shape>();
		strokes = new ArrayList<Stroke>();

		strokes.add(new BasicStroke((float) lw));
		shapes.add(new Line2D.Double(sx, sy, bx, by));

		strokes.add(null);
		int xPoints[] = { (int) (ex), (int) (tx1), (int) (tx2) };
		int yPoints[] = { (int) (ey), (int) (ty1), (int) (ty2) };
		shapes.add(new Polygon(xPoints, yPoints, 3));
	}
}

/**
 * 文字
 */
class StringFigure extends LFigure {

	public final transient String FONT_NAME[] = { Font.DIALOG, Font.DIALOG_INPUT, Font.MONOSPACED, Font.SANS_SERIF, Font.SERIF };
	private static final transient float DASH = 4.0f;

	protected String text; // 文字列
	protected String fontName;
	protected boolean bold;
	protected boolean italic;
	protected double fontSize;// フォント

	protected transient boolean completed;

	public StringFigure(Figure f) {
		super(f);
		type = Figure.STRING_FIGUE;
		// text = null; // デフォルト
		fontName = FONT_NAME[0];
		bold = false;
		italic = false;
		fontSize = 12.0;
		completed = false;
	}

	@Override
	public void deepCopy(Figure f){
		super.deepCopy(f);
		StringFigure f2 = (StringFigure) f;
		this.text = String.copyValueOf(f2.text.toCharArray());
		this.fontName = String.copyValueOf(f2.fontName.toCharArray());
		this.bold = f2.bold;
		this.italic = f2.italic;
		this.fontSize = f2.fontSize;
	}

	@Override
	public void reshape(int x1, int y1, int x2, int y2) {
		super.reshape(x1, y1, x2, y2);
	}

	@Override
	public void complete(int x1, int y1, int x2, int y2) {
		completed = true;
		new ResponseStringDialog();

	}

	@Override
	public void completeTransform() {
		resetTransform();
		setBounds();
	}

	@Override
	public void setBounds() {
		if(text == null) {
			bounds = new Rectangle2D.Double(x, y, width, height);
			return;
		}
		FontRenderContext frc = new FontRenderContext(new AffineTransform(),true,true);
		Font font = new Font(fontName, getFontStyle(), (int)fontSize);
        Rectangle2D rect = font.getStringBounds(text, frc);
        rect = new Rectangle2D.Double(rect.getX() + x, rect.getY() + y + rect.getHeight(), rect.getWidth(), rect.getHeight());
        bounds = affine.createTransformedShape(rect).getBounds2D();
	}

	@Override
	public void draw(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		if (text == null) {
			if(!completed) {
				g2.setPaint(Color.black);
				float dash[] = { DASH, DASH };
				g2.setStroke(new BasicStroke((float) Math.abs(lineWidth), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
						10.0f, dash, 0.0f));
				g2.drawRect((int) x, (int) y, (int) width, (int) height);
				g2.setStroke(new BasicStroke());
			}
		} else {
			g2.setPaint(paint.getPaint(bounds));
			g2.setFont(new Font(fontName, getFontStyle(), (int) fontSize));

			g2.setTransform(synthesis(affine, drawingAffine));
			g2.drawString(text, (int) sx, (int) (sy + fontSize));
			g2.setTransform(new AffineTransform());
		}
	}

	private int getFontStyle() {
		return bold ? (italic ? Font.BOLD | Font.ITALIC : Font.BOLD) : italic ? Font.ITALIC : Font.PLAIN;
	}

	private void thisSetBounds() {
		this.setBounds();
	}

	/**
	 * 文字を入力するダイアログ
	 */
	class ResponseStringDialog extends JDialog implements ActionListener {

		private JLabel lbl;
		private JTextField fld;
		private JComboBox<String> cmb;
		private JCheckBox bl;
		private JCheckBox it;
		private JSpinner spn;
		private JButton btn;

		public ResponseStringDialog() {
			this.setTitle("テキストボックス");
			this.setLayout(new FlowLayout());

			lbl = new JLabel("テキストを入力してください");
			this.add(lbl);

			fld = new JTextField((text == null) ? "" : text);
			fld.setPreferredSize(new Dimension(500, 30));
			fld.addActionListener(this);
			this.add(fld);

			cmb = new JComboBox<String>(FONT_NAME);
			this.add(cmb);

			bl = new JCheckBox("ボールド", bold);
			this.add(bl);

			it = new JCheckBox("イタリック", italic);
			this.add(it);

			spn = new JSpinner(new SpinnerNumberModel(fontSize, 0.0, java.lang.Double.POSITIVE_INFINITY, 1.0));
			spn.setPreferredSize(new Dimension(50, 30));
			this.add(spn);

			btn = new JButton("完了");
			btn.addActionListener(this);
			this.add(btn);

			this.setSize(500, 120);
			this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			this.setResizable(false);
			this.setVisible(true);
		}

		@Override
	public void actionPerformed(ActionEvent e) {
			text = fld.getText();
			fontName = (String) cmb.getSelectedItem();
			bold = bl.isSelected();
			italic = it.isSelected();
			fontSize = (double) spn.getValue();
			thisSetBounds();
			this.dispose();
			model.updated();
		}

	}

}


/**
 * 画像
 */
class ImageFigure extends Figure {

	private static final transient float DASH = 4.0f;

	protected File file;
	protected transient Image image;

	// 引数付きのコンストラクタは継承されないので，コンストラクタを定義
	public ImageFigure(Figure f) {
		super(f);
		type = Figure.IMAGE_FIGUE;
		file = null;
		makeShape();
	}

	@Override
	public void deepCopy(Figure f) {
		super.deepCopy(f);
		ImageFigure f2 = (ImageFigure) f;
		this.file = new File(f2.file.getPath());
		if(file != null && file.isFile() && file.canRead()) {
			try {
				image = ImageIO.read(file);
			} catch (IOException err) {
				err.printStackTrace();
			}
		}

	}

	@Override
	public void reshape(int x1, int y1, int x2, int y2) {
		super.reshape(x1, y1, x2, y2);
		makeShape();
	}

	@Override
	public void makeShape() {
		shape = new Rectangle2D.Double(x, y, width, height);
	}

	@Override
	public void completeTransform() {
		shape = drawingAffine.createTransformedShape(shape);
		resetTransform();
		setBounds();
	}

	@Override
	public void draw(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setPaint(paint.getPaint(bounds));
		if (file == null) {
			g2.setPaint(Color.black);
			float dash[] = { DASH, DASH };
			g2.setStroke(new BasicStroke((float) Math.abs(lineWidth), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
					10.0f, dash, 0.0f));
			g2.draw(bounds);
			g2.setStroke(new BasicStroke());
		} else {

			g2.setTransform(synthesis(affine, drawingAffine));
			g2.drawImage((Image)image, (int)x, (int)y, (int)width, (int)height, null);
			//g2.drawImage(image, (int)getBounds().getX(), (int)getBounds().getY(), (int)getBounds().getWidth(), (int)getBounds().getHeight(),null);
			g2.setTransform(new AffineTransform());
		}
	}

	@Override
	public void complete(int x1, int y1, int x2, int y2) {
		new ResponseFileDialog();
	}


	/**
	 * 画像を選択するダイアログ
	 */
	class ResponseFileDialog extends JDialog implements ActionListener {

		JFileChooser fileChooser;

		public ResponseFileDialog() {
			this.setTitle("画像ファイル選択");
			this.setLayout(new FlowLayout());

			fileChooser = new JFileChooser("画像ファイル");
			javax.swing.filechooser.FileFilter filter = new FileNameExtensionFilter("画像ファイル",
					"jpeg", "bmp", "wbmp", "gif", "JPG", "png", "jpg", "JPEG", "WBMP");
			fileChooser.addChoosableFileFilter(filter);
			fileChooser.setAcceptAllFileFilterUsed(false);
			fileChooser.addActionListener(this);
			this.add(fileChooser);

			this.pack();
			this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			this.setResizable(false);
			this.setVisible(true);
		}

@Override
	public void actionPerformed(ActionEvent e) {
			file = fileChooser.getSelectedFile();
			if(file != null && file.isFile() && file.canRead()) {
				try {
					image = ImageIO.read(file);
					width = image.getWidth(null);
					height = image.getHeight(null);

				} catch (IOException err) {
					err.printStackTrace();
				}
				model.updated();
			}
			this.dispose();
		}

	}
}

/**
 * グループ化された Figure
 */
class GroupFigure extends Figure {

	protected ArrayList<Figure> fig;

	public GroupFigure(ArrayList<Figure> f) {
		super();
		type = Figure.GROUP_FIGUE;
		fig = new ArrayList<Figure>(f);
		setBounds();
	}

	@Override
	public void deepCopy(Figure f) {
		super.deepCopy(f);
		GroupFigure f2 = (GroupFigure) f;
		this.fig = new ArrayList<Figure>();
		for(int i=0; i<f2.fig.size(); i++) {
			Figure fg = new Figure();
			fg.deepCopy(f2.fig.get(i));
			this.fig.add(fg);
		}
	}

	public ArrayList<Figure> getFigures() {
		return fig;
	}


	@Override
	public void setDrawingAffine(int mode, double sx, double sy, double ex, double ey) {
		super.setDrawingAffine(mode, sx, sy, ex, ey);
		for(int i=0; i<fig.size(); i++) {
			// fig.get(i).setDrawingAffine(mode, sx, sy, ex, ey); // それぞれ別のFigureのように変換をする．
			fig.get(i).setDrawingAffine(drawingAffine);
		}
	}


	@Override
	public void completeTransform() {
		for(int i=0; i<fig.size(); i++) {
			fig.get(i).completeTransform();
		}
		resetTransform();
		setBounds();
	}

	@Override
	public void setBounds() {
		Rectangle2D d = fig.get(0).getBounds();
		double x, y, width, height;
		x = d.getX();
		y = d.getY();
		double maxX = x + d.getWidth();
		double maxY = y + d.getHeight();
		for(int i=1; i<fig.size(); i++) {
			fig.get(i).setBounds();
			d = fig.get(i).getBounds();
			if(d.getWidth() == 0 || d.getHeight() == 0) {fig.remove(i); continue;} // 大きさがなければ消す
			if(d.getX() < x) x = d.getX();
			if(d.getY() < y) y = d.getY();
			if(d.getX() + d.getWidth() > maxX) maxX = d.getX() + d.getWidth();
			if(d.getY() + d.getHeight()> maxY) maxY = d.getY() + d.getHeight();
		}
		width = maxX - x;
		height = maxY - y;
		bounds = new Rectangle2D.Double(x, y, width, height);
	}

	@Override
	public void draw(Graphics g) {
		//setBounds();
		for(int i=0; i<fig.size(); i++) {
			fig.get(i).draw(g);
		}
	}

}




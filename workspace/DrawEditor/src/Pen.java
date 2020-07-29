
import java.awt.*;
import java.util.*;

/**
 * 色と形に関するクラス．データを保持しているので，MVCモデルのうちのMにあたるクラス．
 */
public class Pen extends Observable {

	/**
	 * ボタンにアイコンを描画する際の上下の余白
	 */
	private final int BUTTON_MARGIN_X = 10;
	/**
	 * ボタンにアイコンを描画する際の左右の余白
	 */
	private final int BUTTON_MARGIN_Y = 2;

	/**
	 * 形ごとの Button
	 */
	private ArrayList<ShapeButton> shapeBtns;

	/**
	 * 現在選択されている形
	 */
	private int currentShape;
	/**
	 * 現在選択されている色
	 */
	private Color currentColor;
	/**
	 * 現在選択されているグラデーション
	 */
	private LinearGradientPaint currentPaint;
	/**
	 * グラデーションを使用するかどうか
	 */
	private boolean useGradation;
	/**
	 * 現在選択されている線の太さ
	 */
	private double currentLineWidth;

	/**
	 * 新しい Pen を作成する．
	 */
	public Pen() {
		shapeBtns = new ArrayList<ShapeButton>();
		shapeBtns.add(new RectangleButton(this, "□", "四角形"));
		shapeBtns.add(new OvalButton(this, "◯", "楕円"));
		shapeBtns.add(new TerminatorButton(this, "(__)", "端子"));
		shapeBtns.add(new DocumentButton(this, "|txt~|", "文章"));
		shapeBtns.add(new DecisionButton(this, "◇", "条件"));
		shapeBtns.add(new DataButton(this, "/_/", "入出力"));
		shapeBtns.add(new DatabaseButton(this, "DB", "データベース"));
		shapeBtns.add(new PredefinedButton(this, "F(x)", "定義済み処理"));
		shapeBtns.add(new LoopstartButton(this, "for(", "繰り返し開始"));
		shapeBtns.add(new LoopendButton(this, ")for", "繰り返し終了"));
		shapeBtns.add(new OffpageButton(this, "v", "外部結合子"));
		shapeBtns.add(new PreparationButton(this, "<_>", "準備"));
		shapeBtns.add(new InputButton(this, "≤", "入力"));
		shapeBtns.add(new DisplayButton(this, "<_)", "表示"));
		shapeBtns.add(new LineButton(this, " \\ ", "線"));
		shapeBtns.add(new Arrow1Button(this, "→", "矢印"));
		shapeBtns.add(new Arrow2Button(this, "-▷ ", "矢印"));
		shapeBtns.add(new Arrow3Button(this, "- - ▷", "矢印"));
		shapeBtns.add(new Arrow4Button(this, "-▶", "矢印"));
		shapeBtns.add(new FreeButton(this, "〜", "フリーハンド"));
		shapeBtns.add(new StringButton(this, "Aa", "文字"));
		shapeBtns.add(new ImageButton(this, "画像", "画像ファイル"));

		Figure f = new Figure(BUTTON_MARGIN_X, BUTTON_MARGIN_Y, 0, 0, Color.black, 1, null);
		for (int i = 0; i < shapeBtns.size(); i++) {
			int w = (int) shapeBtns.get(i).getPreferredSize().getWidth() - 2 * BUTTON_MARGIN_X;
			int h = (int) shapeBtns.get(i).getPreferredSize().getHeight() - 2 * BUTTON_MARGIN_Y;
			f.setSize(w, h);
			shapeBtns.get(i).setIconFig(shapeBtns.get(i).createFigure(f));
		}

		currentShape = 0;
		currentColor = Color.black;
		currentLineWidth = 1;
		currentPaint = null;
		useGradation = false;

		setChanged();
		notifyObservers();
	}

	/**
	 * 新しい Figure を返す．
	 * @param x 始点の x 座標
	 * @param y 始点の y 座標
	 * @param w 幅
	 * @param h 高さ
	 * @param model DrawModel
	 * @return new Figure();
	 */
	public Figure createFigure(double x, double y, double w, double h, DrawModel model) {
		if(!useGradation || currentPaint == null) {
			return shapeBtns.get(currentShape).createFigure(new Figure(x, y, w, h, currentColor, currentLineWidth, model));
		}else {
			return shapeBtns.get(currentShape).createFigure(new Figure(x, y, w, h, currentPaint, currentLineWidth, model));
		}
	}

	/**
	 * 引数に形のボタンを指定して，現在の形を設定する．
	 * @param sb 形のボタン
	 * @return 変更されたかどうか
	 */
	public boolean setCurrentShape(ShapeButton sb) {
		for (int i = 0; i < shapeBtns.size(); i++) {
			if (shapeBtns.get(i).getClass() == sb.getClass()) {
				currentShape = i;
				updated();
				return true;
			}
		}
		return false;
	}

	/**
	 * 引数に指定された形のボタンが選択されているかどうかを返す．
	 * @param sb 形のボタン
	 * @return 選択されているかどうか
	 */
	public boolean isSelected(ShapeButton sb) {
		return (shapeBtns.get(currentShape) == sb);
	}

	public ArrayList<ShapeButton> getShapeButton() {
		return shapeBtns;
	}

	public Color getCurrentColor() {
		return currentColor;
	}

	public void setColor(Color color) {
		currentColor = color;
		updated();
	}

	public double getCurrentLineWidth() {
		return currentLineWidth;
	}

	public void setLineWidth(double lineWidth) {
		this.currentLineWidth = lineWidth;
		updated();
	}

	public void setCurrentPaint(LinearGradientPaint p) {
		currentPaint = p;
	}

	public boolean getUseGradation() {
		return useGradation;
	}

	public void setUseGradation(boolean use) {
		useGradation = use;
		updated();
	}

	/**
	 * Pen が変更された時に呼び出す．
	 */
	public void updated() {
		setChanged();
		notifyObservers();
	}

}


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * 形を選択するためのボタン．選択されているボタンから新しい Figure が作成される．
 */
public class ShapeButton extends JButton implements ActionListener, Observer {
	protected Pen pen;

	/**
	 * アイコンとして Button に描画する Figure
	 */
	protected Figure icon;

	/**
	 * 新しい ShapeButton を作成する．
	 * @param pen Pen
	 * @param icon ボタンに表示する文字列．
	 * @param name 注釈に表示する文字列．
	 */
	public ShapeButton(Pen pen, String icon, String name) {
		this.pen = pen;
		this.setToolTipText(name);
		this.setSelected(false);
		this.addActionListener(this);
		pen.addObserver(this);
	}

	/**
	 * ボタンに応じた新しいFigureを作成して返す．
	 * @param f 初期化に使用する Figure
	 * @return 新しい Figure
	 */
	public Figure createFigure(Figure f) {
		return null;
	}

	@Override
	/**
	 * ボタンを押されたら，選択された状態にする．
	 */
	public void actionPerformed(ActionEvent e) {
		if (!this.isSelected()) {
			pen.setCurrentShape(this);
		}else {
			pen.updated(); // 選択モードを解除する
		}
	}

	@Override
	/**
	 * 選択された Shape のボタンを選択状態にし，選択されていない Shape のボタンを非選択状態にする．
	 * トグルボタンを実現するための関数．
	 */
	public void update(Observable o, Object arg) {
		this.setSelected(pen.isSelected(this));
	}

	/**
	 * ボタンのアイコンを Figure で描画する．
	 * @param fig 表示する Figure
	 */
	public void setIconFig(Figure fig) {
		icon = fig;
	}

}

/**
 * アイコンを Figure で描画するボタン
 */
class SShapeButton extends ShapeButton {

	public SShapeButton(Pen pen, String icon, String name) {
		super(pen, icon, name);
	}

	@Override
	/**
	 * ボタンのアイコンを描画する．
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (icon != null) {
			icon.draw(g);
		}
	}

}

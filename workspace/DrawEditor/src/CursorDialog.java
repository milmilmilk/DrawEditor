
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * カーソルのダイアログ
 */
public class CursorDialog extends JDialog implements Observer, ActionListener, ChangeListener{

	private DrawModel model;
	private DrawController cont;

	/**
	 * グリッドの間隔を指定するための Spinner
	 */
	private JSpinner stp;
	/**
	 * グリッドの表示、非表示を切り替えるための ToggleButton
	 */
	private JToggleButton tgl;
	/**
	 * カーソルの座標を表示するための Label
	 */
	private JLabel lbl;

	/**
	 * 新しい CursorDialog を作成する．
	 * @param model DrawModel
	 * @param c DrawController
	 * @param x ダイアログの x 座標
	 * @param y ダイアログの y 座標
	 */
	public CursorDialog(DrawModel model, DrawController c, int x, int y) {
		model.addObserver(this);
		this.model = model;

		cont = c;

		// デスクトップのサイズを最大値に設定
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Rectangle desktopBounds = env.getMaximumWindowBounds();
		int stepMax = (int)Math.max(desktopBounds.getWidth(), desktopBounds.getHeight());
		stp = new JSpinner(new SpinnerNumberModel(model.getStep(), 1, stepMax, 1));
		stp.setToolTipText("格子の間隔");
		stp.setPreferredSize(new Dimension(50, 30));
		stp.addChangeListener(this);
		this.add(stp);

		tgl = new JToggleButton("格子を表示", model.isShowStep());
		tgl.addActionListener(this);
		this.add(tgl);

		lbl = new JLabel("(xxxx, yyyy)");
		lbl.setToolTipText("カーソルの座標");
		this.add(lbl);

		this.setTitle("カーソル");
		this.setLayout(new GridLayout(2, 2));
		this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		this.pack();
		this.setBounds(x, y, this.getWidth(), this.getHeight());
		this.setResizable(false);
		this.setVisible(true);
	}

	private String showCursor() {
		return "("+ cont.getCursorX() + ", " + cont.getCursorY() + ")";
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		model.setShowStep(tgl.isSelected());
		model.updated();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		model.setStep((int)stp.getValue());
		model.updated();
	}

	@Override
	public void update(Observable o, Object arg) {
		lbl.setText(showCursor());
		repaint();
	}

}


import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

 /**
  * 形を選択するダイアログ．
  */
class ShapeDialog extends JDialog {

	public ShapeDialog(DrawModel model, Pen pen, int x, int y) {

		JPanel pnl[] = new JPanel[2];
		pnl[0] = new ShapePanel(pen.getShapeButton());
		pnl[1] = new LineWidthPanel(model, pen);
		this.setLayout(new FlowLayout());
		for (int i = 0; i < pnl.length; i++)
			this.add(pnl[i]);

		this.setTitle("形");
		this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		this.pack();
		this.setBounds(x, y, this.getWidth(), this.getHeight());
		this.setResizable(false);
		this.setVisible(true);
	}

}

/**
 * 形を選択する Panel
 */
class ShapePanel extends JPanel {
	public ShapePanel(ArrayList<ShapeButton> sb) {
		this.setBorder(new TitledBorder("形"));
		this.setLayout(new GridLayout(5, 4));
		for (int i = 0; i < sb.size(); i++) {
			this.add(sb.get(i));
		}
	}
}

/**
 * 線の太さと塗りつぶしを設定する Panel
 */
class LineWidthPanel extends JPanel {

	// Slider で設定できる線の太さの最大値
	private final int SLD_MAX = 20;

	private DrawModel model;
	private Pen pen;

	/**
	 * 塗りつぶすかどうか
	 */
	private boolean fill;

	/**
	 * 塗りつぶすかどうかを設定する CheckBox
	 */
	private JCheckBox cbx;
	/**
	 * 線の太さを設定する Slider
	 */
	private JSlider sld;
	/**
	 * 線の太さを設定する Spinner
	 */
	private JSpinner spn;

	/**
	 * 新しい LineWidthPanel を作成する．
	 * @param model DrawModel
	 * @param pen Pen
	 */
	public LineWidthPanel(DrawModel model, Pen pen) {
		this.model = model;
		this.pen = pen;

		this.setBorder(new TitledBorder("線の太さ"));
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		fill = pen.getCurrentLineWidth() < 0;
		cbx = new JCheckBox("塗りつぶし", fill);
		cbx.addActionListener(new CbxListener());
		this.add(cbx);

		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());

		sld = new JSlider(0, SLD_MAX, (int) pen.getCurrentLineWidth());
		sld.addChangeListener(new SldListener());
		sld.setEnabled(!fill);
		panel.add(sld);

		spn = new JSpinner(new SpinnerNumberModel(pen.getCurrentLineWidth(), 0.0, Double.POSITIVE_INFINITY, 1.0));
		spn.setPreferredSize(new Dimension(50, 30));
		spn.addChangeListener(new SpnListener());
		spn.setEnabled(!fill);
		panel.add(spn);
		this.add(panel);

	}

	class CbxListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			fill = !fill;
			pen.setLineWidth(-pen.getCurrentLineWidth());
			sld.setEnabled(!fill);
			spn.setEnabled(!fill);
			model.updated();
		}

	}

	class SldListener implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent e) {
			if (fill)
				return;
			double value = (double) (int) sld.getValue();
			pen.setLineWidth(value);
			spn.setValue(value);
			model.updated();
		}

	}

	class SpnListener implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent e) {
			if (fill) return;
			double value = (double) spn.getValue();
			pen.setLineWidth(value);
			sld.setValue((int) value);
		}

	}

}

/**
 * 色を選択するダイアログ．
 */
class ColorDialog extends JDialog implements ActionListener, ChangeListener, Observer{
	Pen pen;
	DrawModel model;
	DrawController cont;

	/**
	 * 色を選択する ColorChooser
	 */
	private JColorChooser colorChooser;

	/**
	 * グラデーションを設定するための Panel
	 */
	private JPanel gradation;
	/**
	 * グラデーションを使用するかどうか指定するための CheckBox
	 */
	private JCheckBox useGrd;
	/**
	 * グラデーションの方向を設定するための Panel
	 */
	private ArcPanel arc;
	/**
	 * グラデーションを設定するための GradationPanel
	 */
	private GradationPanel grad;
	/**
	 * グラデーションを設定し直す時にリセットするための Button
	 */
	private JButton reset;

	/**
	 * スポイトを使用するための Button
	 */
	public JButton gcb;


	/**
	 * 新しい ColorDialog を作成する．
	 * @param pen Pen
	 * @param model DrawModel
	 * @param cont DrawController
	 * @param x ダイアログの x 座標
	 * @param y ダイアログの y 座標
	 */
	public ColorDialog(Pen pen, DrawModel model, DrawController cont, int x, int y) {
		this.pen = pen;
		pen.addObserver(this);
		this.model = model;
		this.cont = cont;
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

		colorChooser = new JColorChooser(pen.getCurrentColor());
		colorChooser.getSelectionModel().addChangeListener(this);
		this.add(colorChooser);

		gcb = new JButton("スポイト");
		gcb.addActionListener(new GetColorButtonListener());
		this.add(gcb);

		gradation = new JPanel();
		gradation.setLayout(new FlowLayout());
		gradation.setBorder(new TitledBorder("グラデーション"));

		useGrd = new JCheckBox("有効にする", pen.getUseGradation());
		useGrd.addActionListener(this);
		gradation.add(useGrd);

		arc = new ArcPanel(pen);
		gradation.add(arc);

		grad = new GradationPanel(pen, arc, colorChooser);
		gradation.add(grad);

		reset = new JButton("リセット");
		reset.addActionListener(new ResetListener());
		gradation.add(reset);

		this.add(gradation);

		this.setTitle("色");
		this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		this.pack();
		this.setBounds(x, y, this.getWidth(), this.getHeight());
		this.setResizable(false);
		this.setVisible(true);
	}

	@Override
	/**
	 * ColorChooser が変更された時に呼び出される．
	 */
	public void stateChanged(ChangeEvent e) {
		pen.setColor(colorChooser.getColor());
	}

	@Override
	/**
	 * useGrd が押されて，グラデーションの有効・無効が変更された時に呼び出される．
	 */
	public void actionPerformed(ActionEvent e) {
		pen.setUseGradation(useGrd.isSelected());
	}

	/**
	 * リセットボタンが押された時に呼び出される．
	 */
	class ResetListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			grad.reset();
		}

	}

	/**
	 * 「スポイト」が選択された時に呼び出される．
	 */
	class GetColorButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			model.setDrawingFigure(null);
			cont.setGetColorMode(true);
		}

	}

	@Override
	/**
	 * Pen が変更された時に呼び出される．
	 */
	public void update(Observable o, Object arg) {
		colorChooser.setColor(pen.getCurrentColor());
	}

}

/**
 * グラデーションを指定する Panel
 */
class GradationPanel extends JPanel implements MouseListener, MouseMotionListener, Observer{
	Pen pen;
	ArcPanel arc;
	JColorChooser colorChooser;

	/**
	 * どの位置に色があるか
	 */
	ArrayList<Float> fractions;
	/**
	 * 色の変化
	 */
	ArrayList<Color> colors;
	/**
	 * グラデーションの端の繰り返しの種類
	 */
	MultipleGradientPaint.CycleMethod cycleMethod;

	/**
	 * 新しい GradationPanel を作成する．
	 * @param pen Pen
	 * @param arc 角度を調整する ArcPanel
	 * @param colorChooser 現在の色を受け取るための JColorChooser
	 */
	public GradationPanel(Pen pen, ArcPanel arc, JColorChooser colorChooser){
		this.pen = pen;
		pen.addObserver(this);
		this.arc = arc;
		this.colorChooser = colorChooser;
		this.setBackground(Color.white);
		this.setToolTipText("" + 0.0f);
		Dimension d = new Dimension(100, 10);
		this.setSize(d);
		this.setPreferredSize(d);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);

		reset();
	}

	/**
	 * 設定していたグラデーションをリセットして，新しいグラデーションを設定できるようにする．
	 */
	public void reset() {
		fractions = new ArrayList<Float>();
		fractions.add(0.0f);
		fractions.add(1.0f);
		colors = new ArrayList<Color>();
		colors.add(pen.getCurrentColor());
		colors.add(pen.getCurrentColor());
		cycleMethod = MultipleGradientPaint.CycleMethod.NO_CYCLE;
		pen.updated();
	}

	@Override
	/**
	 * 設定しているグラデーションを Panel に描画する．
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		LinearGradientPaint p = createLinearGradientPaint();
		g2.setPaint(p);
		g2.fillRect(0, 0, getWidth(), getHeight());
	}

	/**
	 * 設定しているグラデーションを 長方形(0,0,1,1)に対する LinearGradientPaint として返す．
	 * @return グラデーション
	 */
	public LinearGradientPaint createLinearGradientPaint() {
		float[] frac = new float[fractions.size()];
		Color[] col = new Color[colors.size()];
		for(int i=0; i<fractions.size(); i++) {
			frac[i] = fractions.get(i);
			col[i] = colors.get(i);
		}
		return new LinearGradientPaint(0f, 0f, (float)getWidth(), 0f, frac, col, cycleMethod);
	}

	@Override
	/**
	 * クリックされた位置に現在の色を指定して，グラデーションを更新する．
	 */
	public void mouseClicked(MouseEvent e) {
		Color c = colorChooser.getColor();
		float x = (float)e.getX() / getWidth();
		if(e.getX() < getHeight()) x = 0.0f;
		if(e.getX() > getWidth() - getHeight()) x = 1.0f;
		for(int i=0; i<fractions.size(); i++) {
			if(x < fractions.get(i)) {
				fractions.add(i, x);
				colors.add(i, c);
				break;
			}else if(x == fractions.get(i)){
				colors.remove(i);
				colors.add(i, c);
				break;
			}
		}

		pen.updated();

	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mouseDragged(MouseEvent e) {

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		this.setToolTipText("" + ((float)e.getX() / (float)this.getWidth()));
	}


	/**
	 * Pen が変更されたら呼び出される．
	 */
	public void update(Observable o, Object arg) {

		float[] frac = new float[fractions.size()];
		Color[] col = new Color[colors.size()];
		for(int i=0; i<fractions.size(); i++) {
			frac[i] = fractions.get(i);
			col[i] = colors.get(i);
		}
		float sx = arc.getSx();
		float sy = arc.getSy();
		float ex = arc.getEx();
		float ey = arc.getEy();
		LinearGradientPaint p = new LinearGradientPaint(sx, sy, ex, ey, frac, col, cycleMethod);
		pen.setCurrentPaint(p);

		repaint();
	}

}

/**
 * グラデーションの角度を指定する Panel
 */
class ArcPanel extends JPanel implements MouseListener, MouseMotionListener, Observer{
	private Pen pen;

	/**
	 * グラデーションの方向を決める座標
	 */
	private float ex, ey;

	/**
	 * マウスがクリックされた座標の角度と，その時に選択されていた方向の差
	 */
	private double mArg;

	/**
	 * 新しい ArcPanel を作成する．
	 * @param pen Pen
	 */
	public ArcPanel(Pen pen){
		this.pen = pen;
		pen.addObserver(this);

		this.setToolTipText("" + 0 + "º");
		Dimension d = new Dimension(50, 50);
		this.setSize(d);
		this.setPreferredSize(d);
		this.setBorder(new BevelBorder(BevelBorder.LOWERED));
		this.addMouseListener(this);
		this.addMouseMotionListener(this);

		ex = 1.0f;
		ey = 0.5f;

		this.setToolTipText("" + ((int)(360 - (culcArg(ex, ey, 1.0f, 1.0f) * 180/Math.PI)) % 360) + "º");
	}

	 /**
	  * 方向を表す矢印4を描画する．
	  */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Figure fig = new Figure((1-ex) * getWidth(), (1-ey) * getHeight(), (2*ex-1) * getWidth(), (2*ey-1) * getHeight(), Color.black, 2, null);
		Arrow4Figure arw =  new Arrow4Figure(fig);
		arw.draw(g);
	}

	/**
	 * 開始点の x 座標を返す．
	 * @return 開始点の x 座標
	 */
	public float getSx() {
		return 1-ex;
	}
	/**
	 * 開始点の y 座標を返す．
	 * @return 開始点の y 座標
	 */
	public float getSy() {
		return 1-ey;
	}
	/**
	 * 終点の x 座標を返す．
	 * @return 終点の x 座標
	 */
	public float getEx() {
		return ex;
	}
	/**
	 * 終点の y 座標を返す．
	 * @return 終点の y 座標
	 */
	public float getEy() {
		return ey;
	}

	@Override
	/**
	 * Pen が変更されたときに呼び出される．
	 */
	public void update(Observable o, Object arg) {
		//repaint();
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	/**
	 * クリックされた時の方向の情報を mArg に書き込む．
	 */
	public void mousePressed(MouseEvent e) {
		mArg = culcArg(ex, ey, 1.0f, 1.0f) - culcArg(e.getX(), e.getY(), getWidth(), getHeight());
	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	/**
	 * 方向を設定する．
	 */
	public void mouseDragged(MouseEvent e) {
		this.setToolTipText("" + ((int)(360 - (culcArg(ex, ey, 1.0f, 1.0f) * 180/Math.PI)) % 360) + "º");
		setXY(mArg + culcArg(e.getX(), e.getY(), getWidth(), getHeight()));
	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}

	/**
	 * 座標から角度を計算する．
	 * @param ex 角度を計算する点の x 座標
	 * @param ey 角度を計算する点の y 座標
	 * @param w Panel の幅
	 * @param h Panel の高さ
	 * @return 角度
	 */
	private double culcArg(float ex, float ey, float w, float h) {
		if (ex == w/2.0) return (ey>=h/2.0)? Math.PI/2: -Math.PI/2;
		double atan =  Math.atan((double)(ey - h/2.0)/(ex - w/2.0));
		if(ex < w/2) {
			return atan + Math.PI;
		}else {
			return atan;
		}
	}

	/**
	 * 引数に指定した角度から，座標 (ex, ey) を計算する．
	 * @param arg 角度
	 */
	private void setXY(double arg) {
		ex = 0.5f + (float)Math.cos(arg) / 2.0f;
		ey = 0.5f + (float)Math.sin(arg) / 2.0f;
		repaint();
		pen.updated();
	}


}




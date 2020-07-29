
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

// Viewは，Observerをimplementsする．Modelを監視して，
// モデルが更新されたupdateする．実際には，Modelから
// update が呼び出される．

/**
 * 描画するパネル． DrawModel が変更されると再描画する．
 */
class ViewPanel extends JPanel implements Observer {

	public static final Color CORSOR_COLOR = new Color(0, 0, 0, 0x3f);
	public static final Color GRID_COLOR = new Color(0, 0, 0, 0x7f);

	protected DrawModel model;
	protected DrawController cont;

	/**
	 * 新しい ViewPanel を作成する．
	 * @param m DrawModel
	 * @param c DrawController
	 */
	public ViewPanel(DrawModel m, DrawController c) {

		this.setBackground(Color.white);
		this.addMouseListener(c);
		this.addMouseMotionListener(c);
		this.addKeyListener(c);
		this.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		model = m;
		model.addObserver(this);
		cont = c;
	}

	@Override
	/**
	 * 描画する．
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		// 図形を描く
		model.drawLayer(g);

		// カーソルを描く
		int cx = cont.getCursorX();
		int cy = cont.getCursorY();
		g.setColor(CORSOR_COLOR);
		g.drawLine(cx, 0, cx, cy-1);
		g.drawLine(cx, cy+1, cx, this.getHeight());
		g.drawLine(0, cy, cx-1, cy);
		g.drawLine(cx+1, cy, this.getWidth(), cy);

		// グリッドを描く
		if(model.isShowStep()) {
			int step = model.getStep();
			g.setColor(GRID_COLOR);
			for(int i=step/2; i<this.getWidth(); i+=step) {
				for(int j=step/2; j<this.getHeight(); j+=step) {
					g.drawLine(i, j, i, j);
				}
			}
		}

	    requestFocusInWindow();
	}

	/**
	 * 描画した絵のピクセルデータをファイルに書き出す．
	 */
	public void save() {
		BufferedImage writeImage = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		model.drawLayer(writeImage.getGraphics());

		JFileChooser fileChooser = new JFileChooser();

		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("png", "png"));
		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("gif", "gif"));
		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("jpeg", "jpeg"));
		// fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("bmp", "bmp"));
		// fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("wbmp", "wbmp"));
		fileChooser.setAcceptAllFileFilterUsed(false);
		int selected = fileChooser.showSaveDialog(this);
		if(selected == 0) {
			try {
				ImageIO.write(writeImage, fileChooser.getFileFilter().getDescription(), fileChooser.getSelectedFile());
			} catch (Exception e) {
			  e.printStackTrace();
			}
		}
	}

	/**
	 * ピクセルの色を取得する．
	 * @param x x 座標
	 * @param y y 座標
	 * @return 点 (x, y) の色
	 */
	public Color getColor(int x, int y){
	      int w = this.getWidth();
	      int h = this.getHeight();
	      BufferedImage image = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
	      Graphics2D g = image.createGraphics();
	      this.printAll(g);//ドローパネルをイメージにペイント
	      g.dispose();//解放
	      return new Color(image.getRGB(x, y));
	  }

	@Override
	public void update(Observable o, Object arg) {
		repaint();
	}
}

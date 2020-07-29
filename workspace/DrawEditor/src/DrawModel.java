
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.io.*;

/**
 * 描画モデルのクラス．描画に関連する多くの変数はここで管理されている． Figure の初期位置の設定，レイヤー，選択，編集に関することを行う．
 */
class DrawModel extends Observable implements Observer, Serializable{
	// 選択した図形を囲う描画パターンを設定．
	/**
	 * 選択された図形を囲う長方形の色
	 */
	public final static Color SELECT_COLOR = new Color(0x00, 0x00, 0x00, 0x7f);
	/**
	 * 図形を変形させる時にクリックするマークの大きさ
	 */
	public final static int MARK_SIZE = 9;
	/**
	 * 拡大，縮小マークの色
	 */
	public final static Color RESIZE_MARK_COLOR = new Color(0x00, 0x00, 0xff, 0x7f);
	/**
	 * 回転移動マークの色
	 */
	public final static Color ROTATE_MARK_COLOR = new Color(0x00, 0xff, 0x00, 0x7f);
	/**
	 * シャーリング変換マークの色
	 */
	public final static Color SHEAR_MARK_COLOR = new Color(0xff, 0x00, 0x00, 0x7f);

	// 保持
	private DrawFrame frame;
	private Pen pen;
	ArrayList<Layer> layer;

	/**
	 * 上書き保存するファイル．
	 */
	File outFile;

	/**
	 * グリッドの間隔
	 */
	private int step;
	/**
	 * グリッドを表示するかどうか
	 */
	private boolean showStep;

	/**
	 * レイヤーの名前を自動でつけるために，これまでに作成されたレイヤーの個数を保持します．
	 */
	private int layerNameNumber;
	/**
	 * 選択されているレイヤー
	 */
	private int selectedLayer;
	/**
	 * 選択されているレイヤーを単独表示するかどうか
	 */
	private boolean layerSolo;

	/**
	 * 新しい Figure の初期位置を設定している際に，その Figure が格納されます．
	 */
	private Figure drawingFigure;

	/**
	 * 選択されている Figure のリスト
	 */
	private ArrayList<Figure> selectedFig;
	/**
	 * 選択モードかどうか
	 */
	private boolean selectMode;
	// private Figure editingFig; // 編集時に，一つだけを操作する時に使用します．
	/**
	 * 編集中かどうか（変形の種類を格納します．）
	 */
	private int editMode;

	/**
	 * アニメーションモードかどうか．アニメーションの実行中は ViewPanel の入力を受け付けない．
	 */
	private boolean animationMode;

	/**
	 * 元に戻す操作をするために，操作を記録しておくリスト．
	 */
	private ArrayList<Operation> und;
	/**
	 * やり直しの操作をするために，元に戻した操作を記録しておくリスト．
	 */
	private ArrayList<Operation> red;

	/**
	 * コピーアンドペーストで使用するクリップボード
	 */
	private ArrayList<Figure> clipboard;


	/**
	 * 新しい DrawModel を作成する．
	 * @param frame DrawFrame
	 * @param pen Pen
	 */
	public DrawModel(DrawFrame frame, Pen pen) {
		this.frame = frame;
		this.pen = pen;
		this.pen.addObserver(this);
		step = 1;
		showStep = false;
		initDrawModel();
	}

	/**
	 * DrawModel のフィールド変数を初期化する．
	 */
	private void initDrawModel() {
		drawingFigure = null; // null は定数．C言語のNULLと同じで，何も入っていないという意味．

		layer = new ArrayList<Layer>();
		layer.add(new Layer(0));

		selectedFig = new ArrayList<Figure>();
		selectMode = false;
		// editingFig = null;

		animationMode = false;

		layerNameNumber = 1;
		selectedLayer = 0;

		und = new ArrayList<Operation>();
		red = new ArrayList<Operation>();

		clipboard = new ArrayList<Figure>();

		if(frame.view != null)frame.view.setBackground(Color.white);
	}

	// Figure の初期位置を設定*************************************************************

	/**
	 * 新しい Figure を作成し，選択されているレイヤーに追加する．
	 * @param x クリックされた始点の x 座標
	 * @param y クリックされた始点の y 座標
	 */
	public void createFigure(int x, int y) {
		if(animationMode) return;
		Figure f = pen.createFigure(x, y, 0, 0, this);//new RectangleFigure(x, y, 0, 0, currentColor);
		layer.get(selectedLayer).addFigure(f);
		drawingFigure = f;
		operate(new AddFigureOperation(this, drawingFigure, layer.get(selectedLayer)));
		updated();
	}

	/**
	 * Figure の初期位置を設定している際に，マウスの座標が変更されたら呼び出され，描画し直す．
	 * @param x1 始点の x 座標
	 * @param y1 始点の y 座標
	 * @param x2 終点の x 座標
	 * @param y2 秋天の y 座標
	 */
	public void reshapeFigure(int x1, int y1, int x2, int y2) {
		if(animationMode) return;
		if (drawingFigure != null) {
			drawingFigure.reshape(x1, y1, x2, y2);
			updated();
		}
	}

	/**
	 * Figure の初期位置が設定し終わった時に呼び出される．
	 * @param x1 始点の x 座標
	 * @param y1 始点の y 座標
	 * @param x2 終点の x 座標
	 * @param y2 秋天の y 座標
	 */
	public void completeFigure(int x1, int y1, int x2, int y2) {
		if(animationMode) return;
		if (drawingFigure != null) {
			drawingFigure.complete(x1, y1, x2, y2);
			drawingFigure.setBounds();
			updated();
		}
	}


	public void setDrawingFigure(Figure f) {
		drawingFigure = f;
	}

	// Layer **********************************************************************

	public ArrayList<Layer> getLayer(){
		return layer;
	}

	public int getSelectedLayer() {
		return selectedLayer;
	}

	/**
	 * 引数で指定された Layer を選択する．
	 * @param select 選択する Layer のインデックス
	 */
	public void setSelectedLayer(int select) {
		// if(select < 0 || layer.size() <= select) {System.err.println("DrawModel.setSelectedLayer: ArrayList out of bounds. " + select); return;}
		selectedLayer = select;
		updated();
	}

	/**
	 * 自動で初期化された Layer を追加する．
	 */
	public void addLayer() {
		layer.add(selectedLayer+1, new Layer(layerNameNumber));
		layerNameNumber++;
		setSelectedLayer(selectedLayer + 1);
		operate(new AddLayerOperation(this, selectedLayer, layer.get(selectedLayer)));
		updated();
	}

	/**
	 * i 番目にレイヤー l を追加する．
	 * @param i インデックス
	 * @param l Layer
	 */
	public void addLayer(int i, Layer l) {
		layer.add(i, l);
		layerNameNumber++;
		setSelectedLayer(i);
		updated();
	}

	/**
	 * 選択されている Layer を削除する．
	 */
	public void deleteLayer() {
		if(layer.size() <= 1) return;
		operate(new DeleteLayerOperation(this, selectedLayer, layer.get(selectedLayer)));
		layer.remove(selectedLayer);
		setSelectedLayer(selectedLayer - 1);
		updated();
	}

	/**
	 * 引数で指定されたレイヤーを削除する．
	 * @param l 削除する Layer
	 */
	public void deleteLayer(Layer l) {
		layer.remove(l);
		if(selectedLayer >= layer.size()) selectedLayer = layer.size()-1;
		updated();
	}

	/**
	 * Layer の単独表示を設定する．
	 * @param solo 単独表示をするなら true ，他のレイヤーも表示するなら false ．
	 */
	public void setLayerSolo(boolean solo) {
		layerSolo = solo;
		updated();
	}

	/**
	 * 選択された Layer の表示，非表示を切り替える．
	 * @param mute 非表示にするかどうか
	 */
	public void setLayerMute(boolean mute) {
		layer.get(selectedLayer).setMute(mute);
		operate(new MuteLayerOperation(this, layer.get(selectedLayer), mute));
		updated();
	}

	/**
	 * 描画の際に呼び出され， Layer の中の Figure を描画する．
	 * @param g Graphics
	 */
	public void drawLayer(Graphics g) {
		if(layerSolo) {
			layer.get(selectedLayer).draw(g);
		}else {
			for(int i=0; i<layer.size(); i++) { // 番号の若いレイヤーが後ろ
				if(i!=selectedLayer) layer.get(i).draw(g);
			}
			layer.get(selectedLayer).draw(g); // 選択されているレイヤーは一番上に．
		}

		drawSelect(g);
	}

	// Select *************************************************************************

	/**
	 * 選択されている図形を囲う長方形を描画する．
	 * @param g Graphics
	 */
	public void drawSelect(Graphics g) {
		if(!selectMode) return;
		float DASH = 4.0f;
		Graphics2D g2 = (Graphics2D) g;
		float dash[] = { DASH, DASH };

		for(int i=0; i<selectedFig.size(); i++) {
			Rectangle2D r = selectedFig.get(i).getBounds();
			g2.setTransform(selectedFig.get(i).getDrawingAffine());

			g2.setPaint(SELECT_COLOR);
			g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
			g2.draw(r);

			g2.setStroke(new BasicStroke());

			g2.setPaint(RESIZE_MARK_COLOR);
			int x1 = (int)r.getX();
			int y1 = (int)r.getY();
			int x2 = (int)(r.getX() + r.getWidth()/2 - MARK_SIZE/2);
			int y2 = (int)(r.getY() + r.getHeight()/2 - MARK_SIZE/2);
			int x3 = (int)(r.getX() + r.getWidth() - MARK_SIZE);
			int y3 = (int)(r.getY() + r.getHeight() - MARK_SIZE);
			g2.fillRect(x1, y1, MARK_SIZE, MARK_SIZE);
			g2.fillRect(x1, y2, MARK_SIZE, MARK_SIZE);
			g2.fillRect(x1, y3, MARK_SIZE, MARK_SIZE);
			g2.fillRect(x2, y1, MARK_SIZE, MARK_SIZE);
			g2.fillRect(x2, y3, MARK_SIZE, MARK_SIZE);
			g2.fillRect(x3, y1, MARK_SIZE, MARK_SIZE);
			g2.fillRect(x3, y2, MARK_SIZE, MARK_SIZE);
			g2.fillRect(x3, y3, MARK_SIZE, MARK_SIZE);

			g2.setPaint(ROTATE_MARK_COLOR);
			g2.fillRect(x2, y1+MARK_SIZE, MARK_SIZE, MARK_SIZE);

			g2.setPaint(SHEAR_MARK_COLOR);
			g2.fillRect(x3, y2-MARK_SIZE, MARK_SIZE, MARK_SIZE);
			g2.fillRect(x2-MARK_SIZE, y3, MARK_SIZE, MARK_SIZE);
			g2.fillRect(x1, y2+MARK_SIZE, MARK_SIZE, MARK_SIZE);
			g2.fillRect(x2+MARK_SIZE, y1, MARK_SIZE, MARK_SIZE);

			g2.setTransform(new AffineTransform());
		}
	}

	/**
	 * 図形を選択する．
	 * @param mx クリックされたマウスの x 座標
	 * @param my クリックされたマウスの y 座標
	 * @param add 追加で選択する時 true ，選択し直す時 false
	 */
	private void selectFigure(double mx, double my, boolean add){
		if(animationMode) return;

		if(!add) resetSelected();
		Figure f = layer.get(selectedLayer).selectFigure(mx, my);
		if(f != null) {
			if(selectedFig.contains(f)) {
				selectedFig.remove(f);
			}else {
				selectedFig.add(f);
			}
		}
		updated();
	}

	/**
	 * 図形の選択を解除する．
	 */
	public void resetSelected() {
		selectedFig = new ArrayList<Figure>();
		updated();
	}

	/**
	 * これから変形する図形を選び，変換の種類を指定する．
	 * @param mx クリックされたマウスの x 座標
	 * @param my クリックされたマウスの y 座標
	 */
	public void selectEditFigure(double mx, double my) {
		if(animationMode) return;

		// editingFig = null;
		editMode = 0;
		for(int i=0; i<selectedFig.size(); i++) {
			editMode = selectedFig.get(i).editMode(mx, my);
			if(editMode != 0) {
				// editingFig = selectedFig.get(i);
				return;
			}
		}
	}

	/**
	 * 図形を変形する．
	 * @param sx 始点の x 座標
	 * @param sy 始点の y 座標
	 * @param ex 終点の x 座標
	 * @param ey 終点の y 座標
	 */
	public void editFigure(double sx, double sy, double ex, double ey) {
		if(animationMode) return;
		if(editMode == 0) return;

		for(int i=0; i<selectedFig.size(); i++) {
			selectedFig.get(i).setDrawingAffine(editMode, sx, sy, ex, ey);
		}
		updated();
	}

	/**
	 * 図形の変形が終わった時に呼び出される．
	 * @param x1 始点の x 座標
	 * @param y1 始点の y 座標
	 * @param x2 終点の x 座標
	 * @param y2 終点の y 座標
	 * @param add 追加で選択する時 true ，選択し直す時 false
	 */
	public void resetEditing(int x1, int y1, int x2, int y2, boolean add) {

		if(animationMode) return;

		if(x1==x2 && y1==y2) {selectFigure(x1, y1, add); return;}

		for(int i=0; i<selectedFig.size(); i++) {
			operate(new TransformFigureOperation(this, selectedFig));
			selectedFig.get(i).completeTransform();
			Rectangle2D b = selectedFig.get(i).getBounds();
			if(b.getWidth()==0 || b.getHeight()==0) {
				deleteFigure(selectedFig.get(i));
				i--;
			}
		}
		// editingFig = null;
		editMode = 0;
		updated();
	}

	/**
	 * さらに図形を選択する．
	 * @param f 選択する図形
	 */
	public void addSelectedFigure(Figure f) {
		selectedFig.add(f);
		selectMode = true;
	}

	public ArrayList<Figure> getSelectedFigure(){
		return selectedFig;
	}

	public boolean getSelectMode() {
		return selectMode;
	}

	/**
	 * 選択モードかどうかを指定する．
	 * @param mode 選択モードにするかどうか
	 */
	public void setSelectMode(boolean mode) {
		if(!mode) resetSelected();
		selectMode = mode;
		updated();
	}

	/**
	 * 引数で指定された Figure を削除する．
	 * @param del 削除する Figure
	 */
	public void deleteFigure(Figure del) {
		for(int j=0; j<layer.size(); j++) {
			layer.get(j).removeFigure(del);
		}
		selectedFig.remove(del);
		updated();
	}

	/**
	 * 引数で指定された Figure を削除する．
	 * @param del 削除する Figure
	 */
	public void deleteFigure(ArrayList<Figure> del) {
		while(del.size()!=0) {
			deleteFigure(del.get(0));
			del.remove(0);
		}
	}

	/**
	 * 選択されている Figure を削除する．
	 */
	public void deleteSelectedFigure() {
		operate(new DeleteFigureOperation(this, selectedFig, layer.get(selectedLayer)));
		deleteFigure(new ArrayList<Figure>(selectedFig));
	}

	/**
	 * 複数選択されている Figure をグループ化する．
	 */
	public void grouping() {
		if(selectedFig.size() <= 1) return;

		Figure f = new GroupFigure(selectedFig);//
		operate(new GroupingOperation(this, f, selectedFig, layer.get(selectedLayer)));
		layer.get(selectedLayer).addFigure(f);
		deleteFigure(new ArrayList<Figure>(selectedFig));
		resetSelected();
		selectedFig.add(f);
		updated();
	}

	/**
	 * グループ化された Figure を解除する．
	 */
	public void ungrouping() {
		ArrayList<Figure> sc = new ArrayList<Figure>(selectedFig);
		resetSelected();
		for(int i=0; i<sc.size(); i++) {
			Figure f = sc.get(i);
			ArrayList<Figure> a = f.getFigures();
			if(a == null) continue;
			for(int j=0; j<a.size(); j++) {
				layer.get(selectedLayer).addFigure(a.get(j));
				selectedFig.add(a.get(j));
			}
			deleteFigure(f);
		}
		operate(new UngroupingOperation(this, sc, selectedFig, layer.get(selectedLayer)));
	}

	// 編集 ****************************************************************************

	/**
	 * 操作を記録する．これによって，「元に戻す」操作ができる．
	 * @param c 記録する操作
	 */
	public void operate(Operation c) {
		und.add(c);
		red = new ArrayList<Operation>();
	}

	/**
	 * 操作を戻す．
	 */
	public void undo() {
		if(und.size() == 0) return;
		Operation c = und.get(und.size()-1);
		resetSelected();
		int usize = und.size();
		c.inverse();
		while(usize < und.size()) und.remove(und.size()-1);
		red.add(c);
		und.remove(und.size()-1);
	}

	/**
	 * 戻した操作をやり直す．
	 */
	public void redo() {
		if(red.size() == 0) return;
		Operation c = red.get(red.size()-1);
		resetSelected();
		int usize = und.size();
		c.conversion();
		while(usize < und.size()) und.remove(und.size()-1);
		und.add(c);
		red.remove(red.size()-1);
	}

	/**
	 * 操作を戻せるかどうかを返す．
	 * @return 操作を元に戻せるかどうか
	 */
	public boolean undoable(){
		return und != null && und.size() != 0;
	}

	/**
	 * 操作をやり直せるかどうかを返す．
	 * @return 操作をやり直せるかどうか
	 */
	public boolean redoable(){
		return red != null && red.size() != 0;
	}

	/**
	 * Figure をカットして，クリップボードに記録する．
	 */
	public void cut() {
		if(selectedFig.size() == 0) return;
		clipboard = deepCopy(selectedFig);
		this.deleteSelectedFigure();
	}

	/**
	 * Figure をクリップボードにコピーする．
	 */
	public void copy() {
		if(selectedFig.size() == 0) return;
		clipboard = deepCopy(selectedFig);
	}

	/**
	 * ペーストする．クリップボードに記録された Figure を，選択された Layer に追加する．
	 */
	public void paste() {
		if(clipboard.size() == 0) return;
		resetSelected();
		for(int i=0; i<clipboard.size(); i++) {
			Figure f = cloneFigure(clipboard.get(i));
			layer.get(selectedLayer).addFigure(f);
			addSelectedFigure(f);
		}
		operate(new PasteFigureOperation(this, clipboard, layer.get(selectedLayer)));
		updated();
	}

	/**
	 * Figure のリストをディープコピーした新しい Figure のリストを返す．
	 * @param fg コピー元の Figure のリスト
	 * @return ディープコピーされた Figure のリスト
	 */
	private ArrayList<Figure> deepCopy(ArrayList<Figure> fg){
		ArrayList<Figure> result = new ArrayList<Figure>();
		for(int i=0; i<fg.size(); i++) {
			Figure f = cloneFigure(fg.get(i));
			result.add(f);
		}
		return result;
	}

	/**
	 * Figure をディープコピーする．コピーできる変数をコピーし， Figure の中の Shape を作成し直す．
	 * @param f コピー元の Figure
	 * @return ディープコピーされた Figure
	 */
	private Figure cloneFigure(Figure f) {
		Figure fg = makeFigure(f.getType(), f);
		fg.deepCopy(f);
		fg.makeShape();
		//fg.reshape((int)f.x, (int)f.y, (int)(f.x + f.width), (int)(f.y + f.height));
		//fg.reshape((int)f.x, (int)f.y, (int)(f.x + f.width), (int)(f.y + f.height));
		fg.initAffine();
		fg.setDrawingAffine(f.getAffine());
		fg.completeTransform();
		fg.setBounds();
		return fg;
	}

	/**
	 * 引数で指定されたタイプの Figure を返す．
	 * @param type 作成する Figure のタイプ．
	 * @param arg 初期化に使用する Figure
	 * @return 新しい Figure
	 */
	public Figure makeFigure(int type, Figure arg) {
		switch(type) {
		case Figure. FIGURE:
			return new Figure(arg); //  0;
		case Figure. RECTANGLE_FIGUE :
			return new RectangleFigure(arg); //  1;
		case Figure. OVAL_FIGUE :
			return new OvalFigure(arg); //  2;
		case Figure. TERMINATOR_FIGUE :
			return new TerminatorFigure(arg); //  3;
		case Figure. DOCUMENT_FIGUE :
			return new DocumentFigure(arg); //  4;
		case Figure. DECISION_FIGUE :
			return new DecisionFigure(arg); //  5;
		case Figure. DATA_FIGUE :
			return new DataFigure(arg); //  6;
		case Figure. DATABASE_FIGUE :
			return new DatabaseFigure(arg); //  7;
		case Figure. PREDEFINED_FIGUE :
			return new PredefinedFigure(arg); //  8;
		case Figure. LOOPSTART_FIGUE :
			return new LoopstartFigure(arg); //  9;
		case Figure. LOOPEND_FIGUE :
			return new LoopendFigure(arg); //  10;
		case Figure. OFFPAGE_FIGUE :
			return new OffpageFigure(arg); //  11;
		case Figure. PREPARATION_FIGUE :
			return new PreparationFigure(arg); //  12;
		case Figure. INPUT_FIGUE :
			return new InputFigure(arg); //  13;
		case Figure. DISPLAY_FIGUE :
			return new DisplayFigure(arg); //  14;
		case Figure. L_FIGUE :
			return new LFigure(arg); //  15;
		case Figure. LINE_FIGUE :
			return new LineFigure(arg); //  16;
		case Figure. FREE_FIGUE :
			return new FreeFigure(arg); //  17;
		case Figure. ARW_FIGUE :
			return new ArwFigure(arg); //  18;
		case Figure. ARROW1_FIGUE :
			return new Arrow1Figure(arg); //  19;
		case Figure. ARROW2_FIGUE :
			return new Arrow2Figure(arg); //  20;
		case Figure. ARROW3_FIGUE :
			return new Arrow3Figure(arg); //  21;
		case Figure. ARROW4_FIGUE :
			return new Arrow4Figure(arg); //  22;
		case Figure. STRING_FIGUE :
			return new StringFigure(arg); //  23;
		case Figure. IMAGE_FIGUE :
			return new ImageFigure(arg); //  24;
		case Figure. GROUP_FIGUE :
			return new GroupFigure(arg.getFigures()); //  25;
		default:
			return null;
		}
	}

	/**
	 * ペーストできるかどうかを返す．クリップボードが空のときはペーストできない．
	 * @return ペーストできるかどうか
	 */
	public boolean pastable() {
		return clipboard != null && clipboard.size() != 0;
	}

	/**
	 * 保存する．保存されたファイルを開くことで，続きから作業を再開できる．
	 * @param file 保存するファイル
	 */
	public void save(File file) {
		try {
			ObjectOutput out = new ObjectOutputStream(new FileOutputStream(file));
			out.writeObject(layer);
			out.writeObject(step);
			out.writeObject(showStep);
			out.writeObject(frame.getAnimation());
			out.flush();
			out.close();
			outFile = new File(file.getAbsolutePath());

		}catch(IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 上書き保存する．
	 */
	public void overSave() {
		if(outFile!=null && outFile.isFile()) save(outFile);
		else save();
	}

	/**
	 * 名前をつけて保存する．
	 */
	public void save() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("drw", "drw"));
		fileChooser.setAcceptAllFileFilterUsed(false);
		int selected = fileChooser.showSaveDialog(frame);
		if(selected == 0) save(fileChooser.getSelectedFile());
	}

	/**
	 * 保存したファイルを読み込む．
	 */
	public void load() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("drw", "drw"));
		fileChooser.setAcceptAllFileFilterUsed(false);
		int selected = fileChooser.showOpenDialog(frame);
		if(selected == 0) {
			try {
				ObjectInputStream in=new ObjectInputStream(new FileInputStream(fileChooser.getSelectedFile()));
				layer = (ArrayList<Layer>)in.readObject();
				step = (int)in.readObject();
				showStep = (boolean)in.readObject();
				frame.setAnimation((Animation)in.readObject());
				in.close();

				for(int i=0; i<layer.size(); i++){
					ArrayList<Figure> alf = layer.get(i).getFigure();
					for(int j=0; j<alf.size(); j++) {
						//alf.get(j)
						Figure f = cloneFigure(alf.get(j));
						alf.set(j, f);
					}
				}
				outFile = fileChooser.getSelectedFile();
				updated();

			}catch(IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 新規作成をする．
	 */
	public void renew() {
		initDrawModel();
	}

	public void setStep(int step) {
		this.step = step;
	}

	public int getStep() {
		return step;
	}

	public void setShowStep(boolean show) {
		this.showStep = show;
	}

	public boolean isShowStep() {
		return showStep;
	}

	public void setAnimationMode(boolean mode) {
		animationMode = mode;
	}

	/**
	 * DrawModel が変更された時に呼び出す．
	 */
	public void updated() {
		setChanged();
		notifyObservers();
	}

	@Override
	/**
	 * Pen が変更されたら呼ばれる．
	 */
	public void update(Observable o, Object arg) {
		setSelectMode(false);
	}
}

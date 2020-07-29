
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;

/**
 * Figure を追加する操作
 */
class AddFigureOperation extends Operation {

	protected Figure fig;
	protected Layer layer;

	public AddFigureOperation(DrawModel model, Figure f, Layer l) {
		super(model);
		fig = f;
		layer = l;
	}

	public void conversion() {
		layer.addFigure(fig);
		model.addSelectedFigure(fig);
	}

	public void inverse() {
		layer.removeFigure(fig);
	}

}

/**
 * Figure を削除する操作
 */
class DeleteFigureOperation extends Operation {

	protected ArrayList<Figure> fig;
	protected Layer layer;

	public DeleteFigureOperation(DrawModel model, ArrayList<Figure> f, Layer l) {
		super(model);
		fig = new ArrayList<Figure>(f);
		layer = l;
	}

	public void conversion() {
		for(int i=0; i<fig.size(); i++) {
			layer.removeFigure(fig.get(i));
		}
	}

	public void inverse() {
		for(int i=0; i<fig.size(); i++) {
			layer.addFigure(fig.get(i));
			model.addSelectedFigure(fig.get(i));
		}
	}

}

/**
 * Figure を変形する操作
 */
class TransformFigureOperation extends Operation {

	protected ArrayList<Figure> fig;
	protected ArrayList<AffineTransform> affine;

	public TransformFigureOperation(DrawModel model, ArrayList<Figure> f) {
		super(model);
		fig = new ArrayList<Figure>(f);
		affine = new ArrayList<AffineTransform>();
		for(int i=0; i<fig.size(); i++) {
			affine.add(fig.get(i).getDrawingAffine());
		}
	}

	public void conversion() {
		for(int i=0; i<fig.size(); i++) {
			fig.get(i).setDrawingAffine(affine.get(i));
			fig.get(i).completeTransform();
			model.addSelectedFigure(fig.get(i));
		}
	}

	public void inverse() {
		try {
			for(int i=0; i<fig.size(); i++) {
				fig.get(i).setDrawingAffine(affine.get(i).createInverse());
				fig.get(i).completeTransform();
				model.addSelectedFigure(fig.get(i));
			}
		} catch (NoninvertibleTransformException e) {
			e.printStackTrace();
		}
	}

}


/**
 * 複数の Figure をグループ化する操作
 */
class GroupingOperation extends Operation {

	protected Figure group;
	protected ArrayList<Figure> fig;
	protected Layer layer;

	public GroupingOperation(DrawModel model, Figure g, ArrayList<Figure> f, Layer l) {
		super(model);
		group = g;
		fig = new ArrayList<Figure>(f);
		layer = l;
	}

	public void conversion() {
		for(int i=0; i<fig.size(); i++) {
			layer.removeFigure(fig.get(i));
		}
		layer.addFigure(group);
		model.addSelectedFigure(group);
	}

	public void inverse() {
		layer.removeFigure(group);
		for(int i=0; i<fig.size(); i++) {
			layer.addFigure(fig.get(i));
			model.addSelectedFigure(fig.get(i));
		}
	}

}

/**
 * グループ化された Figure を解除する操作
 */
class UngroupingOperation extends Operation {

	protected ArrayList<Figure> group;
	protected ArrayList<Figure> fig;
	protected Layer layer;

	public UngroupingOperation(DrawModel model, ArrayList<Figure> g, ArrayList<Figure> f, Layer l) {
		super(model);
		group = new ArrayList<Figure>(g);
		fig = new ArrayList<Figure>(f);
		layer = l;
	}

	public void conversion() {
		for(int i=0; i<group.size(); i++) {
			layer.removeFigure(group.get(i));
		}
		for(int i=0; i<fig.size(); i++) {
			layer.addFigure(fig.get(i));
			model.addSelectedFigure(fig.get(i));
		}
	}

	public void inverse() {
		for(int i=0; i<fig.size(); i++) {
			layer.removeFigure(fig.get(i));
		}
		for(int i=0; i<group.size(); i++) {
			layer.addFigure(group.get(i));
			model.addSelectedFigure(group.get(i));
		}
	}

}

/**
 * Layer を追加する操作
 */
class AddLayerOperation extends Operation {

	protected int index;
	protected Layer layer;

	public AddLayerOperation(DrawModel model, int i, Layer l) {
		super(model);
		index = i;
		layer = l;
	}

	public void conversion() {
		model.addLayer(index, layer);
	}

	public void inverse() {
		model.deleteLayer(layer);
	}

}

/**
 * Layer を削除する操作．
 */
class DeleteLayerOperation extends Operation {

	protected int index;
	protected Layer layer;

	public DeleteLayerOperation(DrawModel model, int i, Layer l) {
		super(model);
		index = i;
		layer = l;
	}

	public void conversion() {
		model.deleteLayer(layer);
	}

	public void inverse() {
		model.addLayer(index, layer);
	}

}

/**
 * レイヤーを表示・非表示させる操作
 */
class MuteLayerOperation extends Operation {

	protected Layer layer;
	protected boolean mute;

	public MuteLayerOperation(DrawModel model, Layer l, boolean m) {
		super(model);
		layer = l;
		mute = m;
	}

	public void conversion() {
		layer.setMute(mute);
	}

	public void inverse() {
		layer.setMute(!mute);
	}

}

/**
 * Figure をペーストする操作
 */
class PasteFigureOperation extends Operation {

	protected ArrayList<Figure> fig;
	protected Layer layer;

	public PasteFigureOperation(DrawModel model, ArrayList<Figure> f, Layer l) {
		super(model);
		fig = new ArrayList<Figure>(f);
		layer = l;
	}

	public void conversion() {
		for(int i=0; i<fig.size(); i++) {
			layer.addFigure(fig.get(i));
			model.addSelectedFigure(fig.get(i));
		}
	}

	public void inverse() {
		for(int i=0; i<fig.size(); i++) {
			layer.removeFigure(fig.get(i));
		}
	}

}

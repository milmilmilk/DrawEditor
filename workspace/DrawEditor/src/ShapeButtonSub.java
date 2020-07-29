
class RectangleButton extends SShapeButton {
	public RectangleButton(Pen pen, String icon, String name) {
		super(pen, icon, name);
	}

	@Override
	public Figure createFigure(Figure f) {
		return new RectangleFigure(f);
	}
}

class OvalButton extends SShapeButton {
	public OvalButton(Pen pen, String icon, String name) {
		super(pen, icon, name);
	}

	@Override
	public Figure createFigure(Figure f) {
		return new OvalFigure(f);
	}
}

class TerminatorButton extends SShapeButton {
	public TerminatorButton(Pen pen, String icon, String name) {
		super(pen, icon, name);
	}

	@Override
	public Figure createFigure(Figure f) {
		return new TerminatorFigure(f);
	}
}

class DocumentButton extends SShapeButton {
	public DocumentButton(Pen pen, String icon, String name) {
		super(pen, icon, name);
	}

	@Override
	public Figure createFigure(Figure f) {
		return new DocumentFigure(f);
	}
}

class DecisionButton extends SShapeButton {
	public DecisionButton(Pen pen, String icon, String name) {
		super(pen, icon, name);
	}

	@Override
	public Figure createFigure(Figure f) {
		return new DecisionFigure(f);
	}
}

class DataButton extends SShapeButton {
	public DataButton(Pen pen, String icon, String name) {
		super(pen, icon, name);
	}

	@Override
	public Figure createFigure(Figure f) {
		return new DataFigure(f);
	}
}

class DatabaseButton extends SShapeButton {
	public DatabaseButton(Pen pen, String icon, String name) {
		super(pen, icon, name);
	}

	@Override
	public Figure createFigure(Figure f) {
		return new DatabaseFigure(f);
	}
}

class PredefinedButton extends SShapeButton {
	public PredefinedButton(Pen pen, String icon, String name) {
		super(pen, icon, name);
	}

	@Override
	public Figure createFigure(Figure f) {
		return new PredefinedFigure(f);
	}
}

class LoopstartButton extends SShapeButton {
	public LoopstartButton(Pen pen, String icon, String name) {
		super(pen, icon, name);
	}

	@Override
	public Figure createFigure(Figure f) {
		return new LoopstartFigure(f);
	}
}

class LoopendButton extends SShapeButton {
	public LoopendButton(Pen pen, String icon, String name) {
		super(pen, icon, name);
	}

	@Override
	public Figure createFigure(Figure f) {
		return new LoopendFigure(f);
	}
}

class OffpageButton extends SShapeButton {
	public OffpageButton(Pen pen, String icon, String name) {
		super(pen, icon, name);
	}

	@Override
	public Figure createFigure(Figure f) {
		return new OffpageFigure(f);
	}
}

class PreparationButton extends SShapeButton {
	public PreparationButton(Pen pen, String icon, String name) {
		super(pen, icon, name);
	}

	@Override
	public Figure createFigure(Figure f) {
		return new PreparationFigure(f);
	}
}

class InputButton extends SShapeButton {
	public InputButton(Pen pen, String icon, String name) {
		super(pen, icon, name);
	}

	@Override
	public Figure createFigure(Figure f) {
		return new InputFigure(f);
	}
}

class DisplayButton extends SShapeButton {
	public DisplayButton(Pen pen, String icon, String name) {
		super(pen, icon, name);
	}

	@Override
	public Figure createFigure(Figure f) {
		return new DisplayFigure(f);
	}
}

class LineButton extends SShapeButton {
	public LineButton(Pen pen, String icon, String name) {
		super(pen, icon, name);
	}

	@Override
	public Figure createFigure(Figure f) {
		return new LineFigure(f);
	}
}

class Arrow1Button extends SShapeButton {
	public Arrow1Button(Pen pen, String icon, String name) {
		super(pen, icon, name);
	}

	@Override
	public Figure createFigure(Figure f) {
		return new Arrow1Figure(f);
	}
}

class Arrow2Button extends SShapeButton {
	public Arrow2Button(Pen pen, String icon, String name) {
		super(pen, icon, name);
	}

	@Override
	public Figure createFigure(Figure f) {
		return new Arrow2Figure(f);
	}
}

class Arrow3Button extends SShapeButton {
	public Arrow3Button(Pen pen, String icon, String name) {
		super(pen, icon, name);
	}

	@Override
	public Figure createFigure(Figure f) {
		return new Arrow3Figure(f);
	}
}

class Arrow4Button extends SShapeButton {
	public Arrow4Button(Pen pen, String icon, String name) {
		super(pen, icon, name);
	}

	@Override
	public Figure createFigure(Figure f) {
		return new Arrow4Figure(f);
	}
}

class FreeButton extends ShapeButton {
	public FreeButton(Pen pen, String icon, String name) {
		super(pen, icon, name);
		setText(icon);
	}

	@Override
	public Figure createFigure(Figure f) {
		return new FreeFigure(f);
	}
}

class StringButton extends ShapeButton {
	public StringButton(Pen pen, String icon, String name) {
		super(pen, icon, name);
		setText(icon);
	}

	@Override
	public Figure createFigure(Figure f) {
		return new StringFigure(f);
	}
}


class ImageButton extends ShapeButton {
	public ImageButton(Pen pen, String icon, String name) {
		super(pen, icon, name);
		this.setText(icon);
	}

	@Override
	public Figure createFigure(Figure f) {
		return new ImageFigure(f);
	}
}

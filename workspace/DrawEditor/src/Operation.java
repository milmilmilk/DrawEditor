

/**
 * 操作．操作を記録しておくことで，「元に戻す」「やり直し」ができる．
 */
public class Operation {

	//どの操作を元に戻すか，表示する時に使う
//	public static final int ADD_FIGURE = 11;
//	public static final int DELETE_FIGURE = 12;
//	public static final int TRANSFORM_FIGURE = 13;
//	public static final int GROUPING = 15;
//	public static final int UNGROUPING = 16;
//	public static final int ADD_LAYER = 21;
//	public static final int DELETE_LAYER = 22;
//	public static final int MUTE_LAYER = 23;
//	protected int cmd;

	protected DrawModel model;

	/**
	 * 新しい Operation を作成する．
	 * @param model DrawModel
	 */
	public Operation(DrawModel model) {
		this.model = model;
	}

	/**
	 * 順操作を記述する．
	 */
	public void conversion() {

	}

	/**
	 * 逆操作を記述する．
	 */
	public void inverse() {

	}
}

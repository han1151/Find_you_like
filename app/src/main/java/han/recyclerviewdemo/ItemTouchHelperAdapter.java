package han.recyclerviewdemo;
/**
 * Created by Han on 12/3/15.
 */
public interface ItemTouchHelperAdapter {
    void onItemMove(int fromPosition,int toPosition);
    void onIemDismiss(int position);
}

# SwipeRecyclerView
1.支持侧滑菜单
2.支持长按拖拽
## Maven
1.build.grade
```
allprojects {
    repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
2./app/build.grade
```
dependencies {
	implementation 'com.github.RelinRan:SwipeRecyclerView:2021.12.25.2'
}
```
## 使用方法
### 1.xml布局
```
<com.androidx.widget.SwipeRecyclerView
  android:id="@+id/rv_content"
  android:layout_width="match_parent"
  android:layout_height="match_parent" />
```
### 2.页面代码
```
rv_content = findViewById(R.id.rv_content);
rv_content.setLayoutManager(new LinearLayoutManager(context));
//设置可以长按拖拽
rv_content.setLongPressDragEnabled(true);
//设置可以侧滑显示菜单
rv_content.setSwipeMenuEnable(true);
SwipeAdapter adapter = new SwipeAdapter(this);
adapter.setOnItemClickListener(new SwipeItemClick());
rv_content.setAdapter(adapter);

private class SwipeItemClick implements RecyclerAdapter.OnItemClickListener<String>{
    @Override
    public void onItemClick(RecyclerAdapter<String> adapter, View v, int position) {
       if (v.getId() == R.id.tv_delete) {
            adapter.removeItem(position);
            //操作完毕，必须调用关闭菜单栏
            rv_content.closeSwipeMenu();
       } else if (v.getId() == R.id.tv_edit) {
            //操作完毕，必须调用关闭菜单栏
            rv_content.closeSwipeMenu();
        }
     }
}
```
### 3.侧滑菜单 + itemView
```
    private class SwipeAdapter extends RecyclerAdapter<String> {

        public ItemAdapter(Context context) {
            super(context);
        }

        @Override
        protected int getItemSwipeMenuLayoutResId(int viewType) {
            //菜单布局
            return R.layout.android_menu;
        }

        @Override
        protected int getItemLayoutResId(int viewType) {
            //item布局文件
            return R.layout.androidx_items;
        }

        @Override
        protected void onItemBindViewHolder(ViewHolder holder, int position) {  
            //itemView点击事件
            holder.addItemClick(holder.itemView);
            //找到控件,然后设置参数
            holder.find(TextView.class, R.id.btn_item).setText(getItem(position));
            //添加View点击事件
            holder.addItemClick(R.id.tv_delete);
            holder.addItemClick(R.id.tv_edit);
        }
    }

```
### 常用方法
1.获取触摸事件助手
```
 ItemTouchHelper getItemTouchHelper();
```
2.设置Item触摸助手回调
```
 setSwipeItemTouchHelperCallback(SwipeItemTouchHelperCallback callback);
```
3.设置是否自动处理移动逻辑
```
setDragMoveAuto(boolean dragMoveAuto);
```
4.设置是否自动处理选择逻辑
```
setSelectedAuto(boolean selectedAuto);
```
5.设置是否可长按拖拽
```
setLongPressDragEnabled(boolean longPressDragEnabled) ;
```
6.设置拖动标识
```
setDragFlags(int dragFlags);
```
7.设置滑动标识
```
setSwipeFlags(int swipeFlags);
```
8.设置触摸选中监听
```
setOnItemTouchSelectedChangedListener(OnItemTouchSelectedChangedListener listener);
```
9.设置长按拖拽移动监听
```
setOnItemTouchMoveListener(OnItemTouchMoveListener listener);
```
10.触摸横向滑动完成监听
```
setOnItemTouchSwipedListener(OnItemTouchSwipedListener  listener);
```
11.打开侧滑菜单
```
openSwipeMenu();
```
12.关闭侧滑菜单
```
closeSwipeMenu();
```

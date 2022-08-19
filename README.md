# RecyclerView拖拽合并实现方案

## 简介

基于ItemTouchHelper.Callback接口实现RecyclerView拖拽合并功能，对外提供拖拽处理接口可以根据自身业务需求自定义合并处理逻辑。

提供Adapter和ViewHolder接口，只需要做少量的改动就可以让已有的RecyclerView支持合并Item的效果。

## 效果演示

1、拖拽应用方案

<img src="https://github.com/DSAppTeam/RecyclerViewDrag/blob/master/Screenshot/ds_demo.gif" width = "360" height = "750">

2、合并成文件夹

<img src="https://github.com/DSAppTeam/RecyclerViewDrag/blob/master/Screenshot/folder_demo.gif" width = "360" height = "750">

3、合并执行加法

<img src="https://github.com/DSAppTeam/RecyclerViewDrag/blob/master/Screenshot/addition_demo.gif" width = "360" height = "750">

4、合并执行乘法

<img src="https://github.com/DSAppTeam/RecyclerViewDrag/blob/master/Screenshot/multiply_demo.gif" width = "360" height = "750">

## 配置

1. 在项目根目录下的build.gradle添加 JitPack 仓库

   ```groovy
    allprojects {
        repositories {
            maven { url 'https://jitpack.io' }
        }
    }
   ```
2. 在 **app** 目录下的build.gradle中添加依赖

   ```groovy
   dependencies {
        implementation 'com.github.DSAppTeam:RecyclerViewDrag:1.0.6'
   }
   ```

## 如何使用

```kotlin
val itemTouchCallback = DragTouchCallback(mAdapter)
// 设置自定义的IDragHandler对象
itemTouchCallback.setDragHandler(AdditionHandlerImpl(recyclerView, mAdapter))
ItemTouchHelper(itemTouchCallback).attachToRecyclerView(recyclerView)
```

步骤1：将自己的ViewHolder实现IDragItem接口。

步骤2：将自己的Adapter实现IDragAdapter接口。

步骤3：基于IDragHandler接口实现自己需要的合并处理逻辑。

步骤4：将IDragHandler对象设置给DragTouchCallback，并完成对RecyclerView的绑定。

## 类&接口说明

### IDragItem

描述：拖拽Item接口，由RecyclerView的具体ViewHolder实现，用于判断拖动、合并和显示UI状态等


| 方法名                                                                      | 描述             |
| ----------------------------------------------------------------------------- | ------------------ |
| canDrag(): Boolean                                                          | 是否可以拖动     |
| canMerge(): Boolean                                                         | 是否可以合并     |
| acceptMerge(): Boolean                                                      | 是否接收合并     |
| showMergePreview(holder: RecyclerView.ViewHolder?, show: Boolean)           | 显示合并预览效果 |
| showDragState(holder: RecyclerView.ViewHolder?, isCurrentlyActive: Boolean) | 显示拖动状态     |

### IDragAdapter

描述：拖动适配器，由RecyclerView的具体适配器实现


| 方法名                                                        | 描述                                 |
| --------------------------------------------------------------- | -------------------------------------- |
| getDragData(): List\<Any\>                                    | 获取适配器列表数据                   |
| getDragItem(viewHolder: RecyclerView.ViewHolder?): IDragItem? | 根据ViewHolder获取对应的DragItem对象 |

### IDragHandler

描述：拖拽处理接口，拖拽条件判断、回调监听、合并处理逻辑。接入拖拽功能时需要实现这个接口，并且将这个处理器通过DragTouchCallback#setDragHandler()赋值。


| 方法名                                                    | 描述             |
| ----------------------------------------------------------- | ------------------ |
| swapPosition(fromPosition: Int, toPosition: Int): Boolean | 是否可以交换位置 |
| onBeforeSwap(fromPosition: Int, toPosition: Int)          | 交换位置前回调   |
| onAfterSwap(fromPosition: Int, toPosition: Int)           | 交换位置后回调   |
| onMergeData(fromPosition: Int, toPosition: Int)           | 合并逻辑         |
| onStartDrag(viewHolder: RecyclerView.ViewHolder?)         | 开始拖拽         |
| onStopDrag(performMerge: Boolean)                         | 结束拖拽         |

### DragTouchCallback类

描述：DragTouchCallback继承ItemTouchHelper.Callback()用于判断列表Item在拖拽过程中是否触发Item位置交换、Item合并操作的逻辑处理。


| 方法名                                                                                             | 描述                                     |
| ---------------------------------------------------------------------------------------------------- | ------------------------------------------ |
| DragTouchCallback(dragAdapter: IDragAdapter, horizontal: Boolean = true, vertical: Boolean = true) | 构造方法支持设置垂直、水平方向的拖动控制 |
| setDragHandler(handler: IDragHandler)                                                              | 设置拖拽处理器                           |

**主要实现：**

* 重写chooseDropTarget()方法，计算两个ViewHolder之间X和Y轴方向的重叠部分是否达到触发合并的条件，将满足合并条件的两个ViewHolder暂存起来，当用户松开手指时触发合并逻辑。
* 重写omMove()方法，通过外部注入的IDragHandler处理器的swapPosition()方法判断两个ViewHolder是否可以交换位置。

## 许可证

Apache 2.0. 有关详细信息，请参阅 [License](https://github.com/DSAppTeam/DSDrag/blob/master/LICENSE) 。

## 欢迎提需要支持的功能及issue

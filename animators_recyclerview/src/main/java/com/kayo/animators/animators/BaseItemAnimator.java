package com.kayo.animators.animators;

import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.*;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;

import com.kayo.animators.ViewHelper;
import com.kayo.animators.interfaces.IAnimateHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shilei on 17/1/19.
 * <pre>
 *
 * </pre>
 */

public abstract class BaseItemAnimator extends SimpleItemAnimator implements IAnimateHolder {

    private List<ViewHolder> removeHolders = new ArrayList<>();//移除列表
    private List<ViewHolder> addHolders = new ArrayList<>();//添加列表
    private List<MoveInfo> moveLise = new ArrayList<>();//移动列表
    private List<ChangeInfo> changeList = new ArrayList<>();//更改列表

    private ArrayList<ArrayList<ViewHolder>> addListList = new ArrayList<>();
    private ArrayList<ArrayList<MoveInfo>> moveListList = new ArrayList<>();
    private ArrayList<ArrayList<ChangeInfo>> changeListList = new ArrayList<>();

    private ArrayList<ViewHolder> addAnimations = new ArrayList<>();
    private ArrayList<ViewHolder> moveAnimations = new ArrayList<>();
    private ArrayList<ViewHolder> removeAnimations = new ArrayList<>();
    private ArrayList<ViewHolder> changeAnimations = new ArrayList<>();
    private boolean removed;
    private boolean added;
    private boolean moved;
    private boolean changed;

    //移除数据时调用
    @Override
    public boolean animateRemove(RecyclerView.ViewHolder holder) {
        //结束当前所有动画
        endAnimation(holder);
        ViewHelper.clearAnimStatus(holder.itemView);
        if (holder instanceof IAnimateHolder) {
            ((IAnimateHolder) holder).preAnimateRemoveImpl(holder);
        } else {
            preAnimateRemoveImpl(holder);
        }
        removeHolders.add(holder);
        return true;
    }

    //添加元素时调用，通常返回true
    @Override
    public boolean animateAdd(RecyclerView.ViewHolder holder) {
        endAnimation(holder);
        ViewHelper.clearAnimStatus(holder.itemView);
        if (holder instanceof IAnimateHolder) {
            ((IAnimateHolder) holder).preAnimateAddImpl(holder);
        } else {
            preAnimateAddImpl(holder);
        }
        addHolders.add(holder);
        return true;
    }

    //列表项位置移动时调用
    @Override
    public boolean animateMove(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
        final View view = holder.itemView;
        fromX += ViewCompat.getTranslationX(holder.itemView);
        fromY += ViewCompat.getTranslationY(holder.itemView);
        endAnimation(holder);
        int deltaX = toX - fromX;
        int deltaY = toY - fromY;
        if (deltaX == 0 && deltaY == 0) {
            dispatchMoveFinished(holder);
            return false;
        }
        if (deltaX != 0) {
            ViewCompat.setTranslationX(view, -deltaX);
        }
        if (deltaY != 0) {
            ViewCompat.setTranslationY(view, -deltaY);
        }
        moveLise.add(new MoveInfo(holder, fromX, fromY, toX, toY));
        return true;
    }

    //列表项数据发生改变时调用
    @Override
    public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder,
                                 int fromX, int fromY, int toX, int toY) {
        final float prevTranslationX = ViewCompat.getTranslationX(oldHolder.itemView);
        final float prevTranslationY = ViewCompat.getTranslationY(oldHolder.itemView);
        final float prevAlpha = ViewCompat.getAlpha(oldHolder.itemView);
        endAnimation(oldHolder);
        int deltaX = (int) (toX - fromX - prevTranslationX);
        int deltaY = (int) (toY - fromY - prevTranslationY);
        ViewCompat.setTranslationX(oldHolder.itemView, prevTranslationX);
        ViewCompat.setTranslationY(oldHolder.itemView, prevTranslationY);
        ViewCompat.setAlpha(oldHolder.itemView, prevAlpha);
        if (newHolder != null && newHolder.itemView != null) {
            endAnimation(newHolder);
            ViewCompat.setTranslationX(newHolder.itemView, -deltaX);
            ViewCompat.setTranslationY(newHolder.itemView, -deltaY);
            ViewCompat.setAlpha(newHolder.itemView, 0);
        }
        changeList.add(new ChangeInfo(oldHolder, newHolder, fromX, fromY, toX, toY));
        return true;
    }

    //当有动画需要执行时调用
    @Override
    public void runPendingAnimations() {
        removed = !removeHolders.isEmpty();
        added = !addHolders.isEmpty();
        moved = !moveLise.isEmpty();
        changed = !changeList.isEmpty();

        /*顺序不可更改*/

        //执行删除动画
        doAnimationRemoved(removed);
        //执行移动动画
        doAnimationMoved(moved);
        //执行更改动画
        doAnimationChanged(changed);
        //执行添加动画
        doAnimationAdded(added);
    }

    //当某个动画需要被立即停止时调用，这里一般做视图的状态恢复
    @Override
    public void endAnimation(RecyclerView.ViewHolder item) {
        View view = item.itemView;
        ViewHelper.cancelAnim(view);
        //处理移除
        endAnimationRemove(item);
        //处理添加
        endAnimationAdd(item);
        //处理移动
        endAnimationMove(item);
        //处理更改
        endAnimationChange(item);

        if (!isRunning()) {
            dispatchAnimationsFinished();
        }
    }

    //作用同上，区别是停止多个动画时调用
    @Override
    public void endAnimations() {
        int count = moveLise.size();
        for (int i = count - 1; i >= 0; i--) {
            MoveInfo item = moveLise.get(i);
            View view = item.holder.itemView;
            ViewCompat.setTranslationY(view, 0);
            ViewCompat.setTranslationX(view, 0);
            dispatchMoveFinished(item.holder);
            moveLise.remove(i);
        }
        count = removeHolders.size();
        for (int i = count - 1; i >= 0; i--) {
            ViewHolder item = removeHolders.get(i);
            dispatchRemoveFinished(item);
            removeHolders.remove(i);
        }
        count = addHolders.size();
        for (int i = count - 1; i >= 0; i--) {
            ViewHolder item = addHolders.get(i);
            ViewHelper.clearAnimStatus(item.itemView);
            dispatchAddFinished(item);
            addHolders.remove(i);
        }
        count = changeList.size();
        for (int i = count - 1; i >= 0; i--) {
            ChangeInfo changeInfo = changeList.get(i);
            if (changeInfo != null){
                if (changeInfo.oldHolder != null){
                    endAnimationChange(changeInfo,changeInfo.oldHolder);
                }
                if (changeInfo.newHolder != null){
                    endAnimationChange(changeInfo,changeInfo.newHolder);
                }
            }
        }
        changeList.clear();
        if (!isRunning()) {
            return;
        }

        int listCount = moveListList.size();
        for (int i = listCount - 1; i >= 0; i--) {
            ArrayList<MoveInfo> moves = moveListList.get(i);
            count = moves.size();
            for (int j = count - 1; j >= 0; j--) {
                MoveInfo moveInfo = moves.get(j);
                ViewHolder item = moveInfo.holder;
                View view = item.itemView;
                ViewCompat.setTranslationY(view, 0);
                ViewCompat.setTranslationX(view, 0);
                dispatchMoveFinished(moveInfo.holder);
                moves.remove(j);
                if (moves.isEmpty()) {
                    moveListList.remove(moves);
                }
            }
        }
        listCount = addListList.size();
        for (int i = listCount - 1; i >= 0; i--) {
            ArrayList<ViewHolder> additions = addListList.get(i);
            count = additions.size();
            for (int j = count - 1; j >= 0; j--) {
                ViewHolder item = additions.get(j);
                View view = item.itemView;
                ViewCompat.setAlpha(view, 1);
                dispatchAddFinished(item);
                if (j < additions.size()) {
                    additions.remove(j);
                }
                if (additions.isEmpty()) {
                    addListList.remove(additions);
                }
            }
        }
        listCount = changeListList.size();
        for (int i = listCount - 1; i >= 0; i--) {
            ArrayList<ChangeInfo> changes = changeListList.get(i);
            count = changes.size();
            for (int j = count - 1; j >= 0; j--) {
                ChangeInfo changeInfo = changes.get(j);
                if (changeInfo != null){
                    if (changeInfo.oldHolder != null){
                        endAnimationChange(changeInfo,changeInfo.oldHolder);
                    }
                    if (changeInfo.newHolder != null){
                        endAnimationChange(changeInfo,changeInfo.newHolder);
                    }
                }
                if (changes.isEmpty()) {
                    changeListList.remove(changes);
                }
            }
        }

        cancelAllAnim(removeAnimations);
        cancelAllAnim(addAnimations);
        cancelAllAnim(moveAnimations);
        cancelAllAnim(changeAnimations);

        dispatchAnimationsFinished();
    }

    //返回当前是否有动画正在运行
    @Override
    public boolean isRunning() {
        if (!removeHolders.isEmpty()){
            return true;
        }
        if (!addHolders.isEmpty()){
            return true;
        }
        if (!moveLise.isEmpty()){
            return true;
        }
        if (!changeList.isEmpty()){
            return true;
        }
        if (!addListList.isEmpty()){
            return true;
        }
        if (!moveListList.isEmpty()){
            return true;
        }
        if (!changeListList.isEmpty()){
            return true;
        }
        if (!addAnimations.isEmpty()){
            return true;
        }
        if (!moveAnimations.isEmpty()){
            return true;
        }
        if (!removeAnimations.isEmpty()){
            return true;
        }
        if (!changeAnimations.isEmpty()){
            return true;
        }
        return false;
    }

    @Override
    public void preAnimateAddImpl(ViewHolder holder) {

    }

    @Override
    public void preAnimateRemoveImpl(ViewHolder holder) {

    }

    private void animateChangeImpl(final ChangeInfo changeInfo){
        final ViewHolder holder = changeInfo.oldHolder;
        final View view = holder == null ? null : holder.itemView;
        final ViewHolder newHolder = changeInfo.newHolder;
        final View newView = newHolder != null ? newHolder.itemView : null;
        if (view != null) {
            changeAnimations.add(changeInfo.oldHolder);
            final ViewPropertyAnimatorCompat oldViewAnim =
                    ViewCompat.animate(view).setDuration(getChangeDuration());
            oldViewAnim.translationX(changeInfo.toX - changeInfo.fromX);
            oldViewAnim.translationY(changeInfo.toY - changeInfo.fromY);
            oldViewAnim.alpha(0).setListener(new VpaListenerAdapter() {
                @Override
                public void onAnimationStart(View view) {
                    dispatchChangeStarting(changeInfo.oldHolder, true);
                }

                @Override
                public void onAnimationEnd(View view) {
                    oldViewAnim.setListener(null);
                    ViewCompat.setAlpha(view, 1);
                    ViewCompat.setTranslationX(view, 0);
                    ViewCompat.setTranslationY(view, 0);
                    dispatchChangeFinished(changeInfo.oldHolder, true);
                    changeAnimations.remove(changeInfo.oldHolder);
                    if (!isRunning()) {
                        dispatchAnimationsFinished();
                    }
                }
            }).start();
        }
        if (newView != null) {
            changeAnimations.add(changeInfo.newHolder);
            final ViewPropertyAnimatorCompat newViewAnimation = ViewCompat.animate(newView);
            newViewAnimation.translationX(0).translationY(0).setDuration(getChangeDuration()).
                    alpha(1).setListener(new VpaListenerAdapter() {
                @Override
                public void onAnimationStart(View view) {
                    dispatchChangeStarting(changeInfo.newHolder, false);
                }

                @Override
                public void onAnimationEnd(View view) {
                    newViewAnimation.setListener(null);
                    ViewCompat.setAlpha(newView, 1);
                    ViewCompat.setTranslationX(newView, 0);
                    ViewCompat.setTranslationY(newView, 0);
                    dispatchChangeFinished(changeInfo.newHolder, false);
                    changeAnimations.remove(changeInfo.newHolder);
                    if (!isRunning()) {
                        dispatchAnimationsFinished();
                    }
                }
            }).start();
        }
    }

    private void animateMoveImpl(final ViewHolder holder, int fromX, int fromY, int toX, int toY){
        final View view = holder.itemView;
        final int deltaX = toX - fromX;
        final int deltaY = toY - fromY;
        if (deltaX != 0) {
            ViewCompat.animate(view).translationX(0);
        }
        if (deltaY != 0) {
            ViewCompat.animate(view).translationY(0);
        }
        moveAnimations.add(holder);
        final ViewPropertyAnimatorCompat animation = ViewCompat.animate(view);
        animation.setDuration(getMoveDuration()).setListener(new VpaListenerAdapter() {
            @Override
            public void onAnimationStart(View view) {
                dispatchMoveStarting(holder);
            }

            @Override
            public void onAnimationCancel(View view) {
                if (deltaX != 0) {
                    ViewCompat.setTranslationX(view, 0);
                }
                if (deltaY != 0) {
                    ViewCompat.setTranslationY(view, 0);
                }
            }

            @Override
            public void onAnimationEnd(View view) {
                animation.setListener(null);
                dispatchMoveFinished(holder);
                moveAnimations.remove(holder);
                if (!isRunning()) {
                    dispatchAnimationsFinished();
                }
            }
        }).start();
    }

    protected abstract void animateAddImpl(ViewHolder holder);

    protected abstract void animateRemoveImpl(ViewHolder holder);

    //启动删除条目动画
    private void doAnimationRemoved(boolean removed) {
        if (!removed) {
            return;
        }
        for (ViewHolder holder : removeHolders) {
            if (holder instanceof IAnimateHolder) {
                ((IAnimateHolder) holder).animateRemoveImpl(holder, new DefaultRemoveVpaListener(holder));
            } else {
                animateRemoveImpl(holder);
            }
            removeAnimations.add(holder);
        }
        removeHolders.clear();
    }

    //启动添加条目动画
    private void doAnimationAdded(boolean added) {
        if (!added) {
            return;
        }
        final ArrayList<ViewHolder> additions = new ArrayList<>();
        additions.addAll(addHolders);
        addListList.add(additions);
        addHolders.clear();
        Runnable adder = new Runnable() {
            public void run() {
                for (ViewHolder holder : additions) {
                    if (holder instanceof IAnimateHolder) {
                        ((IAnimateHolder) holder).animateAddImpl(holder, new DefaultAddVpaListener(holder));
                    } else {
                        animateAddImpl(holder);
                    }

                    addAnimations.add(holder);
                }
                additions.clear();
                addListList.remove(additions);
            }
        };
        if (removed || moved || changed) {
            long removeDuration = removed ? getRemoveDuration() : 0;
            long moveDuration = moved ? getMoveDuration() : 0;
            long changeDuration = changed ? getChangeDuration() : 0;
            long totalDelay = removeDuration + Math.max(moveDuration, changeDuration);
            View view = additions.get(0).itemView;
            ViewCompat.postOnAnimationDelayed(view, adder, totalDelay);
        } else {
            adder.run();
        }

    }

    //启动移动条目动画
    private void doAnimationMoved(boolean moved) {
        if (moved) {
            final ArrayList<MoveInfo> moves = new ArrayList<MoveInfo>();
            moves.addAll(moveLise);
            moveListList.add(moves);
            moveLise.clear();
            Runnable mover = new Runnable() {
                @Override
                public void run() {
                    for (MoveInfo moveInfo : moves) {
                        animateMoveImpl(moveInfo.holder, moveInfo.fromX, moveInfo.fromY, moveInfo.toX,
                                moveInfo.toY);
                    }
                    moves.clear();
                    moveListList.remove(moves);
                }
            };
            if (removed) {
                View view = moves.get(0).holder.itemView;
                ViewCompat.postOnAnimationDelayed(view, mover, getRemoveDuration());
            } else {
                mover.run();
            }
        }
    }

    //启动更改条目动画
    private void doAnimationChanged(boolean changed) {
        if (changed) {
            final ArrayList<ChangeInfo> changes = new ArrayList<ChangeInfo>();
            changes.addAll(changeList);
            changeListList.add(changes);
            changeList.clear();
            Runnable changer = new Runnable() {
                @Override
                public void run() {
                    for (ChangeInfo change : changes) {
                        animateChangeImpl(change);
                    }
                    changes.clear();
                    changeListList.remove(changes);
                }
            };
            if (removed) {
                ViewHolder holder = changes.get(0).oldHolder;
                ViewCompat.postOnAnimationDelayed(holder.itemView, changer, getRemoveDuration());
            } else {
                changer.run();
            }
        }
    }

    //结束删除条目动画
    private void endAnimationRemove(ViewHolder item) {
        if (removeHolders.remove(item)) {
            ViewHelper.clearAnimStatus(item.itemView);
            dispatchRemoveFinished(item);
        }
        //清空移动列表
        for (int i = moveListList.size() - 1; i >= 0; i--) {
            ArrayList<MoveInfo> moves = moveListList.get(i);
            for (int j = moves.size() - 1; j >= 0; j--) {
                MoveInfo moveInfo = moves.get(j);
                if (moveInfo.holder == item) {
                    ViewCompat.setTranslationY(item.itemView, 0);
                    ViewCompat.setTranslationX(item.itemView, 0);
                    dispatchMoveFinished(item);
                    moves.remove(j);
                    if (moves.isEmpty()) {
                        moveListList.remove(i);
                    }
                    break;
                }
            }
        }
    }

    //结束添加条目动画
    private void endAnimationAdd(ViewHolder item) {
        if (addHolders.remove(item)) {
            ViewHelper.clearAnimStatus(item.itemView);
            dispatchAddFinished(item);
        }

        //清空添加列表
        for (int i = addListList.size() - 1; i >= 0; i--) {
            ArrayList<ViewHolder> additions = addListList.get(i);
            if (additions.remove(item)) {
                ViewHelper.clearAnimStatus(item.itemView);
                dispatchAddFinished(item);
                if (additions.isEmpty()) {
                    addListList.remove(i);
                }
            }
        }
    }

    //结束移动条目动画
    private void endAnimationMove(ViewHolder item) {
        for (int i = moveLise.size() - 1; i >= 0; i--) {
            MoveInfo moveInfo = moveLise.get(i);
            if (moveInfo.holder == item) {
                ViewCompat.setTranslationY(item.itemView, 0);
                ViewCompat.setTranslationX(item.itemView, 0);
                dispatchMoveFinished(item);
                moveLise.remove(i);
            }
        }
    }

    //结束更改条目动画
    private void endAnimationChange(ViewHolder item) {
        for (int i = changeList.size() - 1; i >= 0; i--) {
            ChangeInfo changeInfo = changeList.get(i);
            if (endAnimationChange(changeInfo, item)) {
                if (changeInfo.oldHolder == null && changeInfo.newHolder == null) {
                    changeList.remove(changeInfo);
                }
            }
        }
        //清空更改列表
        for (int i = changeListList.size() - 1; i >= 0; i--) {
            ArrayList<ChangeInfo> changes = changeListList.get(i);
            endAnimationChange(changes, item);
            if (changes.isEmpty()) {
                changeListList.remove(i);
            }
        }

    }

    //结束更改条目动画
    private boolean endAnimationChange(ChangeInfo changeInfo, ViewHolder item) {
        boolean oldItem = false;
        if (changeInfo.newHolder == item) {
            changeInfo.newHolder = null;
        } else if (changeInfo.oldHolder == item) {
            changeInfo.oldHolder = null;
            oldItem = true;
        } else {
            return false;
        }
        ViewCompat.setAlpha(item.itemView, 1);
        ViewCompat.setTranslationX(item.itemView, 0);
        ViewCompat.setTranslationY(item.itemView, 0);
        dispatchChangeFinished(item, oldItem);
        return true;
    }

    //结束更改条目动画
    private void endAnimationChange(List<ChangeInfo> infoList, ViewHolder item) {
        for (int i = infoList.size() - 1; i >= 0; i--) {
            ChangeInfo changeInfo = infoList.get(i);
            if (endAnimationChange(changeInfo, item)) {
                if (changeInfo.oldHolder == null && changeInfo.newHolder == null) {
                    infoList.remove(changeInfo);
                }
            }
        }
    }

    private void cancelAllAnim(List<ViewHolder> list){
        if (list == null || list.size() == 0){
            return;
        }
        for (int i = list.size() - 1; i >= 0; i--) {
            ViewHelper.cancelAnim(list.get(i).itemView);
        }
    }

    public long getRemoveDelay(final RecyclerView.ViewHolder holder) {
        return Math.abs(holder.getOldPosition() * getRemoveDuration() / 4);
    }

    public long getAddDelay(final RecyclerView.ViewHolder holder) {
        return Math.abs(holder.getAdapterPosition() * getAddDuration() / 4);
    }

    //================内部类==================

    private static class MoveInfo {
        ViewHolder holder;
        int fromX, fromY, toX, toY;

        private MoveInfo(ViewHolder holder, int fromX, int fromY, int toX, int toY) {
            this.holder = holder;
            this.fromX = fromX;
            this.fromY = fromY;
            this.toX = toX;
            this.toY = toY;
        }
    }

    private static class ChangeInfo {
        ViewHolder oldHolder, newHolder;
        int fromX, fromY, toX, toY;

        private ChangeInfo(ViewHolder oldHolder, ViewHolder newHolder) {
            this.oldHolder = oldHolder;
            this.newHolder = newHolder;
        }

        private ChangeInfo(ViewHolder oldHolder, ViewHolder newHolder, int fromX, int fromY, int toX,
                           int toY) {
            this(oldHolder, newHolder);
            this.fromX = fromX;
            this.fromY = fromY;
            this.toX = toX;
            this.toY = toY;
        }

        @Override
        public String toString() {
            return "ChangeInfo{" +
                    "oldHolder=" + oldHolder +
                    ", newHolder=" + newHolder +
                    ", fromX=" + fromX +
                    ", fromY=" + fromY +
                    ", toX=" + toX +
                    ", toY=" + toY +
                    '}';
        }
    }

    protected static class VpaListenerAdapter implements ViewPropertyAnimatorListener {

        @Override
        public void onAnimationStart(View view) {
        }

        @Override
        public void onAnimationEnd(View view) {
        }

        @Override
        public void onAnimationCancel(View view) {
        }
    }

    protected class DefaultAddVpaListener extends VpaListenerAdapter {

        RecyclerView.ViewHolder mViewHolder;

        public DefaultAddVpaListener(final RecyclerView.ViewHolder holder) {
            mViewHolder = holder;
        }

        @Override
        public void onAnimationStart(View view) {
            dispatchAddStarting(mViewHolder);
        }

        @Override
        public void onAnimationCancel(View view) {
            ViewHelper.clearAnimStatus(view);
        }

        @Override
        public void onAnimationEnd(View view) {
            ViewHelper.clearAnimStatus(view);
            dispatchAddFinished(mViewHolder);
            addAnimations.remove(mViewHolder);
            if (!isRunning()) {
                dispatchAnimationsFinished();
            }
        }
    }

    protected class DefaultRemoveVpaListener implements ViewPropertyAnimatorListener {
        RecyclerView.ViewHolder mViewHolder;

        public DefaultRemoveVpaListener(final RecyclerView.ViewHolder holder) {
            mViewHolder = holder;
        }

        @Override
        public void onAnimationStart(View view) {
            dispatchRemoveStarting(mViewHolder);
        }

        @Override
        public void onAnimationCancel(View view) {
            ViewHelper.clearAnimStatus(view);
        }

        @Override
        public void onAnimationEnd(View view) {
            ViewHelper.clearAnimStatus(view);
            dispatchRemoveFinished(mViewHolder);
            removeAnimations.remove(mViewHolder);
            if (!isRunning()) {
                dispatchAnimationsFinished();
            }
        }
    }
}

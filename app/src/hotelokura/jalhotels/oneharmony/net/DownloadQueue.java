package hotelokura.jalhotels.oneharmony.net;

import java.util.ArrayList;

import hotelokura.jalhotels.oneharmony.util.LogUtil;

public class DownloadQueue {
	static final String TAG = "QueueManager";

	private class QueueItem {
		public String tag;
		public GetTaskIF task;
	}

	private int sessionMaxCount;

	private PriorityQueue<QueueItem> queue;
	private int taskCount = 0;

	public DownloadQueue(int sessionMaxCount) {
		this.sessionMaxCount = sessionMaxCount;
		this.queue = new PriorityQueue<QueueItem>();
	}

	synchronized public void clear() {
		queue.clear();
	}

	synchronized public void clear(String tag) {
		for (int i = queue.getPriorityMin(); i <= queue.getPriorityMax(); i++) {
			ArrayList<QueueItem> q = queue.getQueue(i);
			for (int j = q.size() - 1; j >= 0; j--) {
				QueueItem item = q.get(j);
				if (item.tag.equals(tag)) {
					LogUtil.d(TAG, "clear: tag=" + tag);
					q.remove(j);
				}
			}
		}
	}

	synchronized public void offer(String tag, int priority, GetTaskIF task) {
		QueueItem item = new QueueItem();
		item.tag = tag;
		item.task = task;
		queue.offer(priority, item);
		executeNext();
	}

	synchronized public void complited() {
		taskCount--;
		executeNext();
	}

	synchronized private void executeNext() {
		if (taskCount >= sessionMaxCount) {
			/* 同時接続数の制限なので後で実行 */
			return;
		}
		QueueItem item = queue.poll();
		if (item == null) {
			/* キューがなくなったので終了 */
			return;
		}

		GetTaskIF task = item.task;
		executeTask(task);

		/* taskCountが一杯になるまで再帰 */
		executeNext();
	}

	synchronized private void executeTask(GetTaskIF task) {
		taskCount++;
		task.execute();
	}
}

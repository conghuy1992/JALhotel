package hotelokura.jalhotels.oneharmony.net;

import java.util.ArrayList;
import java.util.HashMap;

public class PriorityQueue<T> {
	static final String TAG = "PriorityQueue";

	private int priorityMin = 0;
	private int priorityMax = 5;
    private HashMap<Integer, ArrayList<T>> innerQueue = new HashMap<Integer, ArrayList<T>>();

	public PriorityQueue() {
		super();
		for (int i = priorityMin; i <= priorityMax; i++) {
            this.innerQueue.put(i, new ArrayList<T>());
		}
	}

	public int getPriorityMin() {
		return priorityMin;
	}

	public int getPriorityMax() {
		return priorityMax;
	}
    synchronized public void offer(int priority, T item) {
        if (priority < priorityMin) {
            priority = priorityMin;
        } else if (priority > priorityMax) {
            priority = priorityMax;
        }
        ArrayList<T> offerList = this.innerQueue.get(priority);
        if (offerList == null) {
            offerList = new ArrayList<T>();
        }
        offerList.add(item);
        this.innerQueue.put(priority, offerList);
    }


	public boolean isEmpty() {
		ArrayList<T> q = getNextQueue();
		return (q == null);
	}

	public T peek() {
		ArrayList<T> q = getNextQueue();
		if (q != null) {
			return q.get(0);
		}
		return null;
	}
    synchronized public T poll() {
        ArrayList<T> q = getNextQueue();
        if (q != null) {
            T item = q.get(0);
            q.remove(0);
            return item;
        }
        return null;
    }

    synchronized public void clear() {
        for (int i = priorityMin; i <= priorityMax; i++) {
            ArrayList<T> tList = this.innerQueue.get(i);
            if (tList != null && !tList.isEmpty()) {
                tList.clear();
            }
        }
    }

    synchronized private ArrayList<T> getNextQueue() {
        for (int i = priorityMin; i <= priorityMax; i++) {
            ArrayList<T> tList = this.innerQueue.get(i);
            if (tList != null && !tList.isEmpty()) {
                return tList;
            }
        }
        return null;
    }

    synchronized public ArrayList<T> getQueue(int priority) {
        ArrayList<T> rtn = this.innerQueue.get(priority);
        if(rtn == null) {
            rtn = new ArrayList<T>();
        }
        return rtn;
    }
}

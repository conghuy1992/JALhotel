package hotelokura.jalhotels.oneharmony.net;

public interface AsyncCallback<T> {

	void onSuccess(AsyncResult<T> result);

	void onFailed(AsyncResult<T> result);
}

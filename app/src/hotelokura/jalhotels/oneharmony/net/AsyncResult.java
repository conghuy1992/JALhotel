package hotelokura.jalhotels.oneharmony.net;

import hotelokura.jalhotels.oneharmony.MainApplication;

public class AsyncResult<T> {
	static final String TAG = "AsyncResult";

	public enum ResultStatus {
		RESULT_STATUS_NONE, NOT_CONNECTED_ERROR, RESULT_STATUS_NOT_FOUND_ERROR, RESULT_STATUS_URL_ERROR, RESULT_STATUS_SERVER_ERROR, RESULT_STATUS_NETWORK_ERROR, RESULT_STATUS_UNKOWN_ERROR,
	};

	private ResultStatus status;
	private T content;
	private String lastModified;
	private String message;

	public static <T> AsyncResult<T> createNormalResult(T content,
			String lastModified) {
		return new AsyncResult<T>(content, lastModified,
				ResultStatus.RESULT_STATUS_NONE, null);
	}

	public static <T> AsyncResult<T> createErrorResult(ResultStatus status,
			String message) {
		return new AsyncResult<T>(null, null, status, message);
	}

	public static <T> AsyncResult<T> createErrorResult(ResultStatus status,
			int messageId) {
		String message = MainApplication.getInstance().getResources()
				.getString(messageId);
		return createErrorResult(status, message);
	}

	private AsyncResult(T content, String lastModified, ResultStatus status,
			String message) {
		this.content = content;
		this.lastModified = lastModified;
		this.status = status;
		this.message = message;
	}

	public boolean isError() {
		return (status != ResultStatus.RESULT_STATUS_NONE);
	}

	public ResultStatus getStatus() {
		return status;
	}

	public T getContent() {
		return content;
	}

	public String getLastModified() {
		return lastModified;
	}

	public String getMessage() {
		return message;
	}
}

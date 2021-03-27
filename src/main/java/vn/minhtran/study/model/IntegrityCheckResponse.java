package vn.minhtran.study.model;

public class IntegrityCheckResponse {
	private String type;
	private String title;
	private String id;
	private String[] diagnosticResults;

	public String getType() {
		return type;
	}

	public String getTitle() {
		return title;
	}

	public String getId() {
		return id;
	}

	public String[] getDiagnosticResults() {
		return diagnosticResults;
	}

	public IntegrityCheckResponse setType(String type) {
		this.type = type;
		return this;
	}

	public IntegrityCheckResponse setTitle(String title) {
		this.title = title;
		return this;
	}

	IntegrityCheckResponse setId(String id) {
		this.id = id;
		return this;
	}

	public IntegrityCheckResponse setDiagnosticResults(String[] diagnosticResults) {
		this.diagnosticResults = diagnosticResults;
		return this;
	}

	private IntegrityCheckResponse() {

	}

	public static IntegrityCheckResponse from(String id) {
		IntegrityCheckResponse ret = new IntegrityCheckResponse();
		return ret.setId(id);
	}
}

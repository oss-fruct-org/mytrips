package org.fruct.oss.audioguide.gets;


import org.fruct.oss.audioguide.parsers.AuthRedirectResponse;
import org.fruct.oss.audioguide.parsers.GetsResponse;
import org.fruct.oss.audioguide.parsers.IContent;

public abstract class LoginStage1Request extends GetsRequest {
	public LoginStage1Request(Gets gets) {
		super(gets);
	}

	@Override
	protected String createRequestString() {
		return "<request><params/></request>";
	}

	@Override
	protected String getRequestUrl() {
		return Gets.GETS_SERVER + "/userLogin.php";
	}

	@Override
	protected Class<? extends IContent> getContentClass() {
		return AuthRedirectResponse.class;
	}

	@Override
	protected void onPostProcess(GetsResponse response) {
		super.onPostProcess(response);
	}

	@Override
	protected abstract void onError();
}

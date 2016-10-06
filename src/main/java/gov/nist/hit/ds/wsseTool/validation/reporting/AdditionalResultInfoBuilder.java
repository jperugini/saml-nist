package gov.nist.hit.ds.wsseTool.validation.reporting;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public class AdditionalResultInfoBuilder extends RunListener {

	private static final Logger log = Logger.getLogger(AdditionalResultInfoBuilder.class);

	public List<Description> testsDescriptions = new ArrayList<Description>();
	public List<Failure> optionalTestsNotRun = new ArrayList<Failure>();

	@Override
	public void testStarted(Description description) throws Exception {
		testsDescriptions.add(description);
	}

	@Override
	public void testAssumptionFailure(Failure failure) {
		optionalTestsNotRun.add(failure);
	}

}

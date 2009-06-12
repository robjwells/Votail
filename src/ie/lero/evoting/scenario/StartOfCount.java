package ie.lero.evoting.scenario;

import election.tally.Ballot;
import election.tally.BallotBox;
import election.tally.BallotCounting;
import election.tally.Candidate;
import election.tally.Election;
import election.tally.Report;


/**
 * @author Dermot Cochran
 *
 */
public class StartOfCount implements Scenario {

	protected BallotCounting ballotCounting;
	protected /*@ spec_public @*/ Election parameters;
	protected Candidate candidate1, candidate2;
	protected BallotBox ballotBox;

	/**
	 * Test that the count process is started correctly.
	 */
	public void run () {
		ballotCounting.setup(parameters);
		ballotCounting.load(ballotBox);
		
		//@ assert ballotCounting.getStatus() == BallotCounting.PRECOUNT;
		ballotCounting.count();
		//@ assert ballotCounting.getStatus() == BallotCounting.FINISHED;
		Report report = ballotCounting.report();
		
		// Display results with audit log
		//@ assert 51 == ballotCounting.getQuota();
		System.out.println("Quota: " + ballotCounting.getQuota());
		System.out.println(report.getResults());
		System.out.println(ballotCounting.getDecisionLog());
	}

	//@ ensures parameters != null;
	public StartOfCount() {
		ballotCounting = new BallotCounting();
		parameters = new Election();
		parameters.totalNumberOfSeats = 1;
		parameters.numberOfSeatsInThisElection = 1;
		parameters.numberOfCandidates = 2;
		candidate1 = new Candidate();
		candidate2 = new Candidate();
		ballotBox = new BallotBox();
		
		// Generate 101 ballots between two candidates
		Candidate[] list = {candidate1,candidate2};
		parameters.setCandidateList(list);
 		Ballot ballot = new Ballot();
 		ballot.setFirstPreference(candidate2.getCandidateID());
 		ballotBox.accept(ballot);

		// A close race between two almost equal candidates
		for (int i = 0; i < 50; i++) {
		  ballot = new Ballot();
		  ballot.setFirstPreference(candidate1.getCandidateID());
		  ballotBox.accept(ballot);
		  ballot = new Ballot();
		  ballot.setFirstPreference(candidate2.getCandidateID());
		  ballotBox.accept(ballot);
		}
	}
	
	/**
	 * @param args unused
	 */
	public static void main(String[] args) {
		Scenario scenario = new StartOfCount();
		scenario.run();
	}
}

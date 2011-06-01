package ie.votail.model;

import ie.votail.uilioch.TestData;

import java.io.Serializable;
import java.util.logging.Logger;

import edu.mit.csail.sdg.alloy4compiler.translator.A4Tuple;
import edu.mit.csail.sdg.alloy4compiler.translator.A4TupleSet;

import election.tally.Ballot;
import election.tally.BallotBox;
import election.tally.Candidate;
import election.tally.Constituency;

/**
 * Election Configuration, including generated Ballot Box and related
 * information derived from an Alloy model solution for an Electoral Scenario.
 * 
 * @author Dermot Cochran
 */
public class ElectionConfiguration extends BallotBox implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = 2644255293869580385L;
  
  protected ElectoralScenario scenario;
  
  /* The null value for a candidate ID */
  public static final int NO_CANDIDATE_ID = 0;
  
  public static final int MAX_VOTES = Ballot.MAX_BALLOTS;
  
  protected int numberOfWinners;
  
  protected int numberOfSeats;
  
  protected int numberOfCandidates;
  
  protected transient Logger logger;
  
  //@ invariant numberOfCandidateIDs <= numberOfCandidates;
  protected int numberOfCandidateIDs;
  
  //@ invariant numberOfCandidateIDs <= candidateIDs.length;
  protected int[] candidateIDs;
  
  protected transient int currentBallotID = 0;
  
  /**
   * Create an Election Configuration by deserialisation
   * 
   */
  protected ElectionConfiguration() {
    logger = Logger.getAnonymousLogger();
  }
  
  /**
   * Create an empty Election configuration for a given scenario
   * 
   * @param theScenario The electoral scenario 
   */
  public ElectionConfiguration(ElectoralScenario theScenario) {
    this.scenario = theScenario;
    logger = Logger.getAnonymousLogger();
    candidateIDs = new int[Candidate.MAX_CANDIDATES];
    for (int i = 0; i < candidateIDs.length; i++) {
      candidateIDs[i] = Candidate.NO_CANDIDATE;
    }
  }
  
  /**
   * Extract the list of ballot identifiers from an Alloy tuple set
   * 
   * @param tupleSet
   *          The Alloy tuple set
   */
  public void extractPreferences(/*@ non_null*/A4TupleSet tupleSet) {
    int[] preferences = new int[numberOfCandidates];
    int lengthOfBallot = 0;
    
    for (A4Tuple tuple : tupleSet) {
      if (tuple.arity() == 3) {
        String ballot = tuple.atom(0).substring(7); // prefix = "Ballot$"
        int ballotID = 1 + Integer.parseInt(ballot);
        int preference = Integer.parseInt(tuple.atom(1));
        //@ assert 0 <= preference
        String candidate = tuple.atom(2).substring(10); // prefix = "Candidate$"
        int candidateID = 1 + Integer.parseInt(candidate);
        logger.info("ballot = " + ballotID + ", preference = "
            + (preference + 1) + ", candidate = " + candidateID);
        updateCandidateIDs(candidateID);
        
        if (ballotID != currentBallotID && 0 < lengthOfBallot) {
          currentBallotID = ballotID;
          lengthOfBallot = 0;
          this.accept(preferences); // add these preferences to the ballot box
          preferences = new int[Candidate.MAX_CANDIDATES]; // reset values
        }
        preferences[preference] = candidateID;
        lengthOfBallot++;
      }
      else {
        logger.warning("Unexpected arity for this tuple: " + tuple.toString());
      }
    }
    this.accept(preferences); // add these preferences to the ballot box
  }
  
  //@ ensures numberOfCandidateIDs <= numberOfCandidates;
  protected void updateCandidateIDs(int candidateID) {
    for (int i = 0; i < numberOfCandidateIDs; i++) {
      if (candidateID == candidateIDs[i]) {
        return;
      }
    }
    candidateIDs[numberOfCandidateIDs] = candidateID;
    numberOfCandidateIDs++;
  }
  
  /**
   * Generate a constituency list of Candidates to match the Ballot Box for
   * this scenario
   * 
   * @return The constituency with matching candidate ID numbers
   */
  public Constituency getConstituency() {
    Constituency constituency = new Constituency();
    constituency.setNumberOfSeats(this.numberOfWinners, this.numberOfSeats);
    constituency.load(this.candidateIDs);
    return constituency;
  }
  
  //@ requires 0 < theNumberOfWinners
  //@ ensures this.numberOfWinners == theNumberOfWinners
  public void setNumberOfWinners(int theNumberOfWinners) {
    this.numberOfWinners = theNumberOfWinners;
  }
  
  //@ requires this.numberOfWinners <= theNumberOfSeats
  //@ ensures this.numberOfSeats == theNumberOfSeats
  public void setNumberOfSeats(int theNumberOfSeats) {
    this.numberOfSeats = theNumberOfSeats;
  }
  
  public void setNumberOfCandidates(int theNumberOfCandidates) {
    this.numberOfCandidates = theNumberOfCandidates;
  }
  
  /**
   * Create a new election configuration with the same ballot box and
   * constituency data.
   * 
   * @return
   */
  //@ ensures this.equals(\result);
  public ElectionConfiguration copy() {
    ElectionConfiguration copy = new ElectionConfiguration();
    
    copy.ballots = this.ballots;
    copy.candidateIDs = this.candidateIDs;
    copy.numberOfSeats = this.numberOfSeats;
    copy.numberOfCandidates = this.numberOfCandidates;
    copy.numberOfWinners = this.numberOfWinners;
    
    return copy;
  }
  
  //@ ensures \result == this.scenario;
  public/*@ pure @*/ElectoralScenario getScenario() {
    return this.scenario;
  }
  
  /**
   * @return the numberOfWinners
   */
  public int getNumberOfWinners() {
    return numberOfWinners;
  }
  
  /**
   * @return the numberOfSeats
   */
  public int getNumberOfSeats() {
    return numberOfSeats;
  }
  
  /**
   * @return the numberOfCandidates
   */
  public int getNumberOfCandidates() {
    return numberOfCandidates;
  }
  
  /**
   * @return the numberOfCandidateIDs
   */
  public int getNumberOfCandidateIDs() {
    return numberOfCandidateIDs;
  }
  
  /**
   * @return the candidateIDs
   */
  public int[] getCandidateIDs() {
    return candidateIDs;
  }
  
  /**
   * @return the currentBallotID
   */
  public int getCurrentBallotID() {
    return currentBallotID;
  }
  
  /**
   * @param scenario
   *          the scenario to set
   */
  public void setScenario(ElectoralScenario scenario) {
    this.scenario = scenario;
  }
  
  /**
   * @param numberOfCandidateIDs
   *          the numberOfCandidateIDs to set
   */
  public void setNumberOfCandidateIDs(int numberOfCandidateIDs) {
    this.numberOfCandidateIDs = numberOfCandidateIDs;
  }
  
  /**
   * @param candidateIDs
   *          the candidateIDs to set
   */
  public void setCandidateIDs(int[] candidateIDs) {
    this.candidateIDs = candidateIDs;
  }
  
  /**
   * @param currentBallotID
   *          the currentBallotID to set
   */
  public void setCurrentBallotID(int currentBallotID) {
    this.currentBallotID = currentBallotID;
  }
  
  /**
   * Get the contents of the ballot box
   * 
   * @return The ballots
   */
  public Ballot[] getBallots() {
    return ballots;
  }
  
  /**
   * Prune empty ballots and candidate IDs from data
   * 
   * @return The minimal ballot configuration
   */
  public ElectionConfiguration trim() {
    ElectionConfiguration copy = new ElectionConfiguration();
    
    copy.ballots = new Ballot[this.numberOfBallots];
    for (int i = 0; i < this.numberOfBallots; i++) {
      copy.ballots[i] = this.ballots[i];
    }
    copy.candidateIDs = new int[this.numberOfCandidates];
    for (int j = 0; j < this.numberOfCandidates; j++) {
      copy.candidateIDs[j] = this.candidateIDs[j];
    }
    
    copy.numberOfCandidates = this.numberOfCandidates;
    copy.numberOfWinners = this.getNumberOfWinners();
    copy.scenario = this.getScenario();
    copy.currentBallotID = this.currentBallotID;
    copy.index = this.index;
    copy.numberOfBallots = this.numberOfBallots;
    copy.numberOfSeats = this.numberOfSeats;
    return copy;
  }

  /**
   * Export the test data for serialization
   * 
   * @return The data needed for serialization
   */
  public /*@ pure @*/ TestData export() {
    TestData testData = new TestData();
    testData.setScenario(scenario.canonical());
    BallotBox ballotBox = new BallotBox();
    for (index = 0; index < numberOfBallots; index++) {
      ballotBox.accept(ballots[index].getPreferenceList());
    }
    testData.setBallotBox(ballotBox);
    
    return testData;
  }
  
  /**
   * Load deserialised test data
   * 
   * @param testData
   */
  public ElectionConfiguration (TestData testData) {
    logger = Logger.getAnonymousLogger();
    this.scenario = testData.getScenario().canonical();
    candidateIDs = new int[Candidate.MAX_CANDIDATES];
    for (int i = 0; i < candidateIDs.length; i++) {
      candidateIDs[i] = Candidate.NO_CANDIDATE;
    }
    
    BallotBox ballotBox = testData.getBallotBox();
    while (ballotBox.isNextBallot()) {
      final int[] preferenceList = ballotBox.getNextBallot().getPreferenceList();
      this.accept(preferenceList);
      // Recreate the anonymous list of candidates
      for (int preference = 0; preference < preferenceList.length; preference++) {
        updateCandidateIDs(preferenceList[preference]);
      }
    }
  }
}
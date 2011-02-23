// 2010-2011, Dermot Cochran, IT University of Copenhagen

package ie.votail.model.factory.test;

import ie.votail.model.ElectionConfiguration;
import ie.votail.model.ElectoralScenario;
import ie.votail.model.Method;
import ie.votail.model.Outcome;
import ie.votail.model.factory.BallotBoxFactory;
import ie.votail.model.factory.ScenarioFactory;
import ie.votail.model.factory.ScenarioList;

import java.util.logging.Logger;

import org.testng.annotations.Test;

import election.tally.BallotCounting;
import election.tally.Constituency;

public class VotailSystemTest {
  @Test
  public void prstv() {
    
    final int numberOfSeats = 5;
    final int scope = numberOfSeats;
    
    ScenarioFactory scenarioFactory = new ScenarioFactory();
    BallotCounting ballotCounting = new BallotCounting();
    Logger logger = Logger.getLogger(BallotBoxFactory.LOGGER_NAME);
    logger.info("Using scope = " + scope);
    
    int total = 0;
    
    for (int seats = 1; seats <= numberOfSeats; seats++) {
      for (int candidates = 1 + seats; candidates <= 1 + seats * seats; candidates++) {
        
        ScenarioList scenarioList =
            scenarioFactory.find(candidates, seats, Method.STV);
        
        for (ElectoralScenario scenario : scenarioList) {
          logger.info(scenario.toString());
          ElectionConfiguration electionConfiguration =
              createElection(scenario);
          Constituency constituency = electionConfiguration.getConstituency();
          ballotCounting.setup(constituency);
          ballotCounting.load(electionConfiguration);
          ballotCounting.count();
          logger.info(ballotCounting.getResults());
          logger.info(ballotCounting.getNumberOfRounds()
              + " rounds of counting ");
          if (!scenario.check(ballotCounting)) {
            logFailure(logger, scenario, electionConfiguration);
          }
          total++;
        }
      }
    }
  }
  
  /**
   * Create an election configuration, including constituency and ballot box.
   * 
   * @param scenario
   *          The scenario for which to create this configuration
   * 
   * @return The election configuration
   */
  protected/*@ non_null @*/ElectionConfiguration createElection(
      ElectoralScenario scenario) {
    BallotBoxFactory ballotBoxFactory = new BallotBoxFactory();
    ElectionConfiguration electionConfiguration =
        ballotBoxFactory.extractBallots(scenario, scenario
            .getNumberOfCandidates());
    return electionConfiguration;
  }
  
  /**
   * Log information for failed or skipped scenarios.
   * 
   * @param logger
   *          The logging service to use
   * @param scenario
   *          The scenario which failed or was skipped
   * @param electionConfiguration
   *          The ballot box and candidates for this scenario
   */
  protected void logFailure(Logger logger, ElectoralScenario scenario,
      ElectionConfiguration electionConfiguration) {
    
    if (scenario.hasOutcome(Outcome.TiedSoreLoser)
        || electionConfiguration.size() == 0) {
      logger.info("Skipped this scenario " + scenario.toString());
    }
    else {
      logger.severe("Unexpected results for scenario " + scenario
          + " using predicate " + scenario.toPredicate() + " and ballot box "
          + electionConfiguration);
    }
  }
  
  public static void main(String[] args) {
    VotailSystemTest universalTest = new VotailSystemTest();
    universalTest.plurality();
    universalTest.prstv();
  }
  
  @Test
  public void plurality() {
    
    final int numberOfCandidates = 7;
    final int seats = 1;
    
    ScenarioFactory scenarioFactory = new ScenarioFactory();
    BallotCounting ballotCounting = new BallotCounting();
    Logger logger = Logger.getLogger(BallotBoxFactory.LOGGER_NAME);
    
    for (int candidates = 1 + seats; candidates <= numberOfCandidates; candidates++) {
      
      final int scope = candidates;
      logger.info("Using scope = " + scope);
      
      ScenarioList scenarioList =
          scenarioFactory.find(candidates, seats, Method.Plurality);
      
      for (ElectoralScenario scenario : scenarioList) {
        logger.info(scenario.toString());
        ElectionConfiguration electionConfiguration = createElection(scenario);
        Constituency constituency = electionConfiguration.getConstituency();
        ballotCounting.setup(constituency);
        ballotCounting.load(electionConfiguration);
        ballotCounting.count();
        logger.info(ballotCounting.getResults());
        if (!scenario.check(ballotCounting)) {
          logFailure(logger, scenario, electionConfiguration);
        }
      }
    }
  }
}
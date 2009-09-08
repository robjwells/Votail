/**
 * Votail Cuntais - Irish PR-STV ballot counting system
 * 
 * Copyright (c) 2005 Dermot Cochran and Joseph R. Kiniry
 * Copyright (c) 2006,2007 Dermot Cochran, Joseph R. Kiniry and Patrick E. Tierney
 * Copyright (c) 2008,2009 Dermot Cochran
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package election.tally;

/** Data transfer structure for set of all valid ballots */
public class BallotBox {
	
/**
 * List of valid ballot papers.
 */
  protected /*@ spec_public @*/ Ballot[] ballots;
  
  /**
   * No two ballot IDs in a ballot box are the same.
   */
  /*@ public invariant uniqueness();
    @
    @ ensures \result <==> (\forall int i, j;
    @                   0 <= i & i < size() &
    @                   0 <= j & j < size() &
    @                   i != j;
    @                   ballots[i].getBallotID() 
    @                   != ballots[j].getBallotID());
    @
    @ public model pure boolean uniqueness() {
    @     for (int i=0; i < size(); i++) {
    @         for (int j=0; j < size(); j++) {
    @             if (i != j && ballots[i].getBallotID() == 
    @                           ballots[j].getBallotID()) {
    @                 return false;
    @             }
    @         }
    @      }
    @      return true;
    @ }
    @*/

    /**
     * Get the number of ballots in this box.
     * 
     * @return the number of ballots in this ballot box
     */	
   /*@ public normal_behavior
     @   ensures 0 <= \result;
     @   ensures \result == numberOfBallots;
     @   ensures (ballots == null) ==> \result == 0;
     @*/
    public /*@ pure @*/ int size(){
        return numberOfBallots;
    }
	
  /**
   * The total number of ballots in this ballot box.
   */
    /*@ public invariant 0 <= numberOfBallots;
      @ public invariant numberOfBallots <= Ballot.MAX_BALLOTS;
      @ public constraint \old (numberOfBallots) <= numberOfBallots;
      @*/
	protected /*@ spec_public @*/ int numberOfBallots;
	
	/**
	 * Number of ballots copied from box
	 */
	//@ public initially index == 0;
	//@ public invariant index <= size();
	//@ public constraint \old(index) <= index;
 	protected /*@ spec_public @*/ int index;
	
	/**
	 * Create an empty ballot box.
	 */
	public /*@ pure @*/ BallotBox(){
		index = 0;
		numberOfBallots = 0;
		ballots = new Ballot[Ballot.MAX_BALLOTS];
	}

	/**
	 * Accept an anonymous ballot paper.
	 * <p>
	 * The ballot ID number is regenerated.
	 * <p>
	 * @param ballot The ballot paper
	 */
	/*@ requires numberOfBallots < ballots.length;
	  @ ensures \old(numberOfBallots) + 1 == numberOfBallots;
	  @*/
	public void accept (final /*@ non_null @*/ Ballot ballot) {
		ballots[numberOfBallots++] = new Ballot(ballot);
	} 

	/**
	 * Is there another ballot paper?
	 * @return <code>true</code>if there is
	 */
	//@ ensures \result <==> index < numberOfBallots;
	public /*@ pure @*/ boolean isNextBallot() {
		return index < numberOfBallots;
	}

	/**
	 * Get the next ballot paper
	 * @return The ballot paper
	 */
	//@ requires 0 <= index;
	//@ requires isNextBallot();
	//@ assignable index;
	//@ ensures \result == ballots[\old(index)];
    public Ballot getNextBallot() {
      return ballots[index++];
    }
}
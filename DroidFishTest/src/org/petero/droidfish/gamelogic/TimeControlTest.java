/*
    DroidFish - An Android chess program.
    Copyright (C) 2011  Peter Österlund, peterosterlund2@gmail.com

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.petero.droidfish.gamelogic;

import junit.framework.TestCase;


public class TimeControlTest extends TestCase {
    public TimeControlTest() {
    }

    public void testElapsedTime() {
        TimeControl tc = new TimeControl();
        long totTime = 5 * 60 * 1000;
        long t0 = 1000;
        tc.setTimeControl(totTime, 0, 0);
        tc.setCurrentMove(1, true, totTime, totTime);
        assertEquals(0, tc.getMovesToTC());
        assertEquals(0, tc.getIncrement());
        assertEquals(totTime, tc.getRemainingTime(true, 0));
        tc.startTimer(t0);
        int remain = tc.moveMade(t0 + 1000, true);
        assertEquals(totTime - 1000, remain);

        tc.setCurrentMove(2, true, totTime - 1000, totTime);
        assertEquals(0, tc.getMovesToTC());
        assertEquals(totTime - 1000, tc.getRemainingTime(true, t0 + 4711));
        assertEquals(totTime, tc.getRemainingTime(false, t0 + 4711));

        tc.setCurrentMove(1, false, totTime - 1000, totTime);
        assertEquals(0, tc.getMovesToTC());
        assertEquals(totTime - 1000, tc.getRemainingTime(true, t0 + 4711));
        assertEquals(totTime, tc.getRemainingTime(false, t0 + 4711));

        tc.startTimer(t0 + 3000);
        assertEquals(totTime - 1000, tc.getRemainingTime(true, t0 + 5000));
        assertEquals(totTime - 2000, tc.getRemainingTime(false, t0 + 5000));
        tc.stopTimer(t0 + 8000);
        assertEquals(totTime - 1000, tc.getRemainingTime(true, t0 + 4711));
        assertEquals(totTime - 5000, tc.getRemainingTime(false, t0 + 4711));
        remain = tc.moveMade(t0 + 8000, true);
        assertEquals(totTime - 5000, remain);
        tc.setCurrentMove(2, true, totTime - 1000, totTime - 5000);
        assertEquals(totTime - 1000, tc.getRemainingTime(true, t0 + 4711));
        assertEquals(totTime - 5000, tc.getRemainingTime(false, t0 + 4711));
    }

    /** Test getMovesToTC */
    public void testTimeControl() {
        TimeControl tc = new TimeControl();
        tc.setTimeControl(2 * 60 * 1000, 40, 0);
        tc.setCurrentMove(1, true, 0, 0);
        assertEquals(40, tc.getMovesToTC());
        tc.setCurrentMove(1, false, 0, 0);
        assertEquals(40, tc.getMovesToTC());

        tc.setCurrentMove(2, true, 0, 0);
        assertEquals(39, tc.getMovesToTC());

        tc.setCurrentMove(40, true, 0, 0);
        assertEquals(1, tc.getMovesToTC());

        tc.setCurrentMove(41, true, 0, 0);
        assertEquals(40, tc.getMovesToTC());

        tc.setCurrentMove(80, true, 0, 0);
        assertEquals(1, tc.getMovesToTC());

        tc.setCurrentMove(81, true, 0, 0);
        assertEquals(40, tc.getMovesToTC());
    }

    public void testExtraTime() {
        TimeControl tc = new TimeControl();
        final long timeCont = 60 * 1000;
        int wBaseTime = (int)timeCont;
        int bBaseTime = (int)timeCont;
        final long inc = 700;
        tc.setTimeControl(timeCont, 5, inc);
        tc.setCurrentMove(5, true, wBaseTime, bBaseTime);
        long t0 = 1342134;
        assertEquals(timeCont, tc.getRemainingTime(true, t0 + 4711));
        assertEquals(timeCont, tc.getRemainingTime(false, t0 + 4711));

        tc.startTimer(t0 + 1000);
        wBaseTime = tc.moveMade(t0 + 2000, true);
        tc.setCurrentMove(5, false, wBaseTime, bBaseTime);
        assertEquals(timeCont - 1000 + timeCont + inc, tc.getRemainingTime(true, t0 + 4711));
        assertEquals(timeCont, tc.getRemainingTime(false, t0 + 4711));

        tc.startTimer(t0 + 2000);
        bBaseTime = tc.moveMade(t0 + 6000, true);
        tc.setCurrentMove(6, true, wBaseTime, bBaseTime);
        assertEquals(timeCont - 1000 + timeCont + inc, tc.getRemainingTime(true, t0 + 4711));
        assertEquals(timeCont - 4000 + timeCont + inc, tc.getRemainingTime(false, t0 + 4711));

        tc.startTimer(t0 + 6000);
        wBaseTime = tc.moveMade(t0 + 9000, true);
        tc.setCurrentMove(6, false, wBaseTime, bBaseTime);
        assertEquals(timeCont - 1000 + timeCont + inc - 3000 + inc, tc.getRemainingTime(true, t0 + 4711));
        assertEquals(timeCont - 4000 + timeCont + inc, tc.getRemainingTime(false, t0 + 4711));

        // No increment when move made int paused mode, ie analysis mode
        tc.startTimer(t0 + 9000);
        bBaseTime = tc.moveMade(t0 + 10000, false);
        tc.setCurrentMove(7, true, wBaseTime, bBaseTime);
        assertEquals(timeCont - 1000 + timeCont + inc - 3000 + inc, tc.getRemainingTime(true, t0 + 4711));
        assertEquals(timeCont - 4000 + timeCont + inc - 1000, tc.getRemainingTime(false, t0 + 4711));

        // No extra time when passing time control in analysis mode
        tc.setTimeControl(timeCont, 1, inc);
        wBaseTime = bBaseTime = (int)timeCont;
        tc.setCurrentMove(1, true, wBaseTime, bBaseTime);
        tc.startTimer(t0 + 1000);
        wBaseTime = tc.moveMade(t0 + 3000, false);
        tc.setCurrentMove(1, false, wBaseTime, bBaseTime);
        assertEquals(timeCont - 2000 + (timeCont + inc)*0, tc.getRemainingTime(true, t0 + 4711));
        assertEquals(timeCont, tc.getRemainingTime(false, t0 + 4711));
    }
}

package ht.wt.test;
// Generated by ComTest BEGIN
import java.io.*;
import java.util.*;
import static org.junit.Assert.*;
import org.junit.*;
import ht.wt.*;
// Generated by ComTest END

/**
 * Test class made by ComTest
 * @version 2020.05.23 12:37:53 // Generated by ComTest
 *
 */
@SuppressWarnings("all")
public class PaivatTest {


  // Generated by ComTest BEGIN  // Paivat: 41

  private Paivat paivat; 
  private String tiedNimi; 
  private File ftied; 

  @Before
  public void alusta() throws SailoException {
     tiedNimi = "testiweathertracker"; 
     ftied = new File(tiedNimi+".db"); 
     ftied.delete(); 
     paivat = new Paivat(tiedNimi); 
  }

  @After
  public void siivoa() {
     ftied.delete(); 
  }
  // Generated by ComTest END


  // Generated by ComTest BEGIN
  /** 
   * testLisaa127 
   * @throws SailoException when error
   */
  @Test
  public void testLisaa127() throws SailoException {    // Paivat: 127
    Collection<Paiva> loytyneet = paivat.etsi("", 1); 
    assertEquals("From: Paivat line: 131", 0, loytyneet.size()); 
    Paiva pvm1 = new Paiva(), pvm2 = new Paiva(); 
    paivat.lisaa(pvm1); 
    paivat.lisaa(pvm2); 
    loytyneet = paivat.etsi("", 1); 
    assertEquals("From: Paivat line: 138", 2, loytyneet.size()); 
    try {
    paivat.lisaa(pvm1); 
    fail("Paivat: 141 Did not throw SailoException");
    } catch(SailoException _e_){ _e_.getMessage(); }
    Paiva pvm3 = new Paiva(); Paiva pvm4 = new Paiva(); Paiva pvm5 = new Paiva(); 
    paivat.lisaa(pvm3); 
    paivat.lisaa(pvm4); 
    paivat.lisaa(pvm5); 
    loytyneet = paivat.etsi("", 1); 
    assertEquals("From: Paivat line: 148", 5, loytyneet.size()); 
    Iterator<Paiva> i = loytyneet.iterator(); 
    assertEquals("From: Paivat line: 150", pvm1, i.next()); 
    assertEquals("From: Paivat line: 151", pvm2, i.next()); 
    assertEquals("From: Paivat line: 152", pvm3, i.next()); 
  } // Generated by ComTest END


  // Generated by ComTest BEGIN
  /** 
   * testEtsi175 
   * @throws SailoException when error
   */
  @Test
  public void testEtsi175() throws SailoException {    // Paivat: 175
    Paiva pvm1 = new Paiva(); pvm1.taytaPvmTiedoilla(); 
    Paiva pvm2 = new Paiva(); pvm2.taytaPvmTiedoilla(); 
    paivat.lisaa(pvm1); 
    paivat.lisaa(pvm2); 
    try {
    paivat.lisaa(pvm2); 
    fail("Paivat: 181 Did not throw SailoException");
    } catch(SailoException _e_){ _e_.getMessage(); } // ei saa lisätä sama id:tä uudelleen
    Collection<Paiva> loytyneet = paivat.etsi(pvm1.getPvm(), 1); 
    assertEquals("From: Paivat line: 183", 1, loytyneet.size()); 
    assertEquals("From: Paivat line: 184", pvm1, loytyneet.iterator().next()); 
    loytyneet = paivat.etsi(pvm2.getPvm(), 1); 
    assertEquals("From: Paivat line: 186", 1, loytyneet.size()); 
    assertEquals("From: Paivat line: 187", pvm2, loytyneet.iterator().next()); 
    try {
    loytyneet = paivat.etsi("", 15); 
    fail("Paivat: 188 Did not throw SailoException");
    } catch(SailoException _e_){ _e_.getMessage(); }
    ftied.delete(); 
  } // Generated by ComTest END
}
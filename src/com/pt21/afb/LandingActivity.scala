package com.pt21.afb

import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.view._
import android.widget.AdapterView
import com.google.android.glass.app.Card
import com.google.android.glass.media.Sounds
import com.google.android.glass.touchpad.{Gesture, GestureDetector}
import com.google.android.glass.widget.{CardScrollAdapter, CardScrollView}

/**
 * Created by prt2121 on 8/30/14.
 */
class LandingActivity extends Activity {

  private var mScrollView: Option[CardScrollView] = None
  private var mView: Option[View] = None
  private var mGestureDetector: Option[GestureDetector] = None


  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)

    mView = Some(buildView)

    mScrollView = Some(new CardScrollView(this))
    mScrollView.map { scrollView =>
      scrollView.setAdapter(new CardScrollAdapter() {
        override def getCount: Int = 1

        override def getPosition(item: scala.Any): Int =
          if (item == mView) 0
          else AdapterView.INVALID_POSITION

        override def getView(position: Int, convertView: View, parent: ViewGroup): View = mView.get

        override def getItem(position: Int): AnyRef = mView
      })

    }

    mGestureDetector = Some(new GestureDetector(this).setBaseListener(new GestureBaseListener));

    setContentView(mScrollView.get)
  }

  override def onResume(): Unit = {
    super.onResume()
    mScrollView.map(_.activate)
  }

  override def onPause(): Unit = {
    super.onPause()
    mScrollView.map(_.deactivate)
  }

  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    getMenuInflater().inflate(R.menu.menu_main, menu)
    true
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = item.getItemId match {
    case R.id.menu_main_new_game => println("New Game"); true
    case R.id.menu_main_high_score => println("High Score"); true
    case R.id.menu_main_instructions => println("Instructions"); true
    case R.id.menu_main_credit => println("Credit"); true
    case _ => super.onOptionsItemSelected(item)
  }

  override def onGenericMotionEvent(event: MotionEvent): Boolean =
    mGestureDetector.exists(_.onMotionEvent(event))

  def buildView: View = {
    new Card(this).setText("Yeah").getView
  }

  class GestureBaseListener extends GestureDetector.BaseListener {
    override def onGesture(gesture: Gesture): Boolean = {
      println("onGesture " + gesture.name())
      gesture match {
        //TODO: get Gesture.LONG_PRESS when tap
        case Gesture.TAP | Gesture.LONG_PRESS => onTap()
        case _ => false
      }
    }
  }

  def playSound(sound: Int): Unit = {
    val am = getSystemService(Context.AUDIO_SERVICE).asInstanceOf[AudioManager]
    am.playSoundEffect(sound)
  }

  def onTap(): Boolean = {
    playSound(Sounds.TAP)
    openOptionsMenu()
    true
  }

}


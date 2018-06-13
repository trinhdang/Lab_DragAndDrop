package dang.daniel.project.ames.ac.nz.draganddrop;

import android.content.ClipData;
import android.content.ClipDescription;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity implements View.OnDragListener, View.OnLongClickListener{
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //Declare variables and objects
    private static final String TAG = "DragAndDrop";
    private ImageView soccerBall, tennisBall, rugbyBall, tennisRacket;
    private LinearLayout topContainerLayout, bottomContainerLayout;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ///////////////////////////////////////////////////////////////////////
        //Find references and do casting
        soccerBall = (ImageView)findViewById(R.id.soccerBall);
        rugbyBall = (ImageView)findViewById(R.id.rugbyBall);
        tennisBall = (ImageView)findViewById(R.id.tennisBall);
        tennisRacket = (ImageView)findViewById(R.id.tennisRacket);

        topContainerLayout = (LinearLayout)findViewById(R.id.top_container);
        bottomContainerLayout = (LinearLayout)findViewById(R.id.bottom_container);

        ///////////////////////////////////////////////////////////////////////
        //Set listeners: Register drag event listeners for the target layout containers
        soccerBall.setOnLongClickListener(this);
        rugbyBall.setOnLongClickListener(this);
        tennisBall.setOnLongClickListener(this);

        //topContainerLayout.setOnDragListener(this);
        bottomContainerLayout.setOnDragListener(this);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    @Override
    public boolean onLongClick(View imageView) {
        //Called when a ball has been touched and held.
        //When the ball has been touch, create clip data holding data of the type MINETYPE_TEXT_PLAIN
        ClipData clipData = ClipData.newPlainText("", "");
        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(imageView);

        //Start the drag - contains the data to be dragged, metadata for this data and callback for drawing shadow
        //startDrag() was very recently deprecated - in API 24, so we use startDragAndDrop() instead
       if (Build.VERSION.SDK_INT >=24 ) {
           imageView.startDragAndDrop(clipData, shadowBuilder, imageView,0);
       } else {
           //no inspection depreciation
           imageView.startDrag(clipData, shadowBuilder, imageView,0);
       }
        //We're dragging the shadow so make the View visible
        imageView.setVisibility(View.INVISIBLE);

        return true;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    @Override
    public boolean onDrag(View receivingLayoutView, DragEvent dragEvent) {
        //Called when the ball starts to be dragged.
        //Be used by top-layout container and bottom-layout container.
        //Retrieve the View which has been dragged
        View draggedImageView = (View) dragEvent.getLocalState();

        //Handle each type of the expected drag events: ACTION_DRAG_STARTED, ACTION_DRAG_ENTERED,
        //ACTION_DRAG_LOCATION, ACTION_DRAG_EXITED, ACTION_DRAG_DROP, and ACTION_DRAG_ENDED
        switch (dragEvent.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                //It's a drag and it just got started
                Log.i(TAG,"Drag action just got started");
                //Determine if this View can accept the dragged data
                if (dragEvent.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    Log.i(TAG,"Can accept this data");
                    //Return true to indicate that the View can accept the dragged data
                    return true;
                } else {
                    Log.i(TAG,"Can not accept this data");
                }
                //Returns false. During the current drag and drop operation, this View will not receive
                //events again until ACTION_DRAG_ENDED is sent.
                return false;

            case DragEvent.ACTION_DRAG_ENTERED:
                //The drag point has entered the bounding box
                Log.i(TAG,"Drag has entered the bounding box");
                return true;

            case DragEvent.ACTION_DRAG_LOCATION:
                //Triggered after ACTION_DRAG_ENTERED and Stops after ACTION_DRAG_EXITED
                Log.i(TAG,"Drag action location");
                return true;

            case DragEvent.ACTION_DRAG_EXITED:
                //The drag shadow has left the bounding box
                Log.i(TAG,"Drag action exited");
                return true;

            case DragEvent.ACTION_DROP:
                //The listener receives this action type when drag shadow released over the target View.
                //The action only sent here if ACTION_DRAG_STARTED returned TRUE.
                //We will return TRUE if successfully handled the drop, else return FALSE.
                switch (draggedImageView.getId()) {
                    case R.id.soccerBall:
                        // Wrong ball
                        Log.i(TAG,"Soccer Ball - Wrong");
                        return false;
                    case R.id.rugbyBall:
                        // Wrong ball
                        Log.i(TAG,"Rugby Ball - Wrong");
                        return false;
                    case R.id.tennisBall:
                        //Right ball
                        Log.i(TAG,"Tennis Ball - Right");
                        ViewGroup draggedImageViewParentLayout = (ViewGroup) draggedImageView.getParent();
                        draggedImageViewParentLayout.removeView(draggedImageView);
                        bottomContainerLayout = (LinearLayout) receivingLayoutView;
                        bottomContainerLayout.addView(draggedImageView);
                        draggedImageView.setVisibility(View.VISIBLE);
                        return true;
                    default:
                        //Unknown action
                        Log.i(TAG,"In default");
                        return false;
                }

            case DragEvent.ACTION_DRAG_ENDED:
                //
                Log.i(TAG,"Drag action ended");
                Log.i(TAG,"getResult: " + dragEvent.getResult());
                //If the drop was not successful, set the ball to visible
                if (!dragEvent.getResult()) {
                    Log.i(TAG,"Setting ball visible");
                    draggedImageView.setVisibility(View.VISIBLE);
                }
                return true;

            default:
                //Errors or Exceptions: An unknown action type ws received
                Log.i(TAG,"in default");
                break;
        }
        return false;
    }
}

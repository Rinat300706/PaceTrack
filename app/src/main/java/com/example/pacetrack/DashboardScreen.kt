package com.example.pacetrack

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.material3.RadioButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.Button
import androidx.compose.runtime.MutableState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.TextField
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.material3.HorizontalDivider
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.Card
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.OutlinedButton
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import android.app.ActivityOptions
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.provider.Settings
import android.net.Uri
import androidx.compose.runtime.DisposableEffect
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import androidx.compose.runtime.LaunchedEffect
import com.google.android.gms.maps.CameraUpdateFactory
import androidx.compose.material3.MaterialTheme
import com.google.maps.android.compose.rememberUpdatedMarkerState
import com.google.maps.android.compose.Polyline
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.shape.RoundedCornerShape
import com.google.maps.android.compose.MarkerComposable
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.mutableLongStateOf
import kotlinx.coroutines.delay
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.res.stringResource


fun calculateAveragePaceSecondsPerKm(
    elapsedMillis: Long,
    distanceMeters: Float
): Long? {

    // Wait until at least 20 metres have been recorded.
    if (elapsedMillis <= 0L || distanceMeters < 20f) {
        return null
    }

    // Convert the elapsed time into seconds.
    val elapsedSeconds = elapsedMillis / 1000.0

    // Convert GPS distance into kilometres.
    val distanceKm = distanceMeters / 1000.0

    // Calculate how many seconds one kilometre takes.
    return (elapsedSeconds / distanceKm).toLong()
}

@Composable
fun WelcomeMessage() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "PaceTrack",
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp
        )

        Text(stringResource(R.string.home_subtitle))
    }
}

@Composable
fun RadioButtonGroup(
    radio_options: List<String>,
    selected: Int,
    onStateChanged: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        for (i in 0 until radio_options.size) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selected == i,
                    onClick = { onStateChanged(i) }
                )

                Text(radio_options[i])
            }
        }
    }
}

@Composable
fun GoalControls(
    goal_enabled: MutableState<Boolean>,
    activity_options: List<String>,
    selected_activity: MutableState<Int>,
    goal_distance: MutableState<String>
) {


    Text(
        text = stringResource(R.string.workout_choose_activity),
        fontWeight = FontWeight.Bold
    )



    RadioButtonGroup(
        radio_options = listOf(
            stringResource(R.string.history_walk),
            stringResource(R.string.history_run)
        ),
        selected = selected_activity.value
    ) {
        selected_activity.value = it
    }


    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = stringResource(R.string.workout_enable_goal),
            fontWeight = FontWeight.Bold
        )


        Checkbox(
            checked = goal_enabled.value,
            onCheckedChange = {
                goal_enabled.value = it
            }
        )
    }

    if (goal_enabled.value) {


        Text(
            text = stringResource(R.string.workout_distance_target),
            fontWeight = FontWeight.Bold
        )


        TextField(
            value = goal_distance.value,
            onValueChange = {
                goal_distance.value = it
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            ),
            modifier = Modifier.fillMaxWidth()
        )


        Text(
            text = stringResource(
                R.string.workout_goal_display,
                goal_distance.value
            )
        )

    }
}


@Composable
fun DashboardScreen() {

    val context = LocalContext.current

    val workoutStage = remember { mutableIntStateOf(0) }

    // Use Liam's workout timer.
    val workoutTimer = remember { WorkoutTimer() }

    // Connect the dashboard to Liam's Room database.
    val workoutViewModel: WorkoutViewModel = viewModel()

    // Store the elapsed time for displaying on screen.
    val elapsedMillis = remember { mutableLongStateOf(0L) }

    // Store when the workout started for Activity History.
    val workoutStartTime = remember { mutableLongStateOf(0L) }

    // Store the latest GPS reading.
    val latestGpsLocation = remember {
        mutableStateOf<android.location.Location?>(null)
    }
    // Store the distance so the screen updates when GPS changes.
    val distanceMeters = remember {
        mutableStateOf(0f)
    }

    // Show an error if GPS cannot start.
    val gpsError = remember {
        mutableStateOf<String?>(null)
    }

    // Create one GPS tracker for this screen.

    val locationTracker = remember(context) {
        LocationTracker(context) { location, distance ->

            latestGpsLocation.value = location
            distanceMeters.value = distance
        }
    }


    // Start GPS during a workout and stop it when leaving.

    // Observe whether PaceTrack is in the foreground.
    val lifecycleOwner = LocalLifecycleOwner.current

    // Manage GPS when entering and leaving the workout.
    DisposableEffect(
        workoutStage.intValue,
        locationTracker,
        lifecycleOwner
    ) {

        val isWorkoutActive = workoutStage.intValue == 2
        var gpsRunning = false

        // Reset distance only for a new workout.
        if (isWorkoutActive) {
            locationTracker.resetDistance()
            latestGpsLocation.value = null
            distanceMeters.value = 0f
        }

        // Start GPS only when needed.
        fun startTracking() {
            if (isWorkoutActive && !gpsRunning) {
                val started = locationTracker.start()
                gpsRunning = started

                gpsError.value = if (started) {
                    null
                } else {
                    context.getString(R.string.gps_start_error)
                }
            }
        }

        // Stop GPS to avoid unnecessary tracking.
        fun stopTracking() {
            if (gpsRunning) {
                locationTracker.stop()
                gpsRunning = false
            }
        }

        // Listen for activity lifecycle changes.

        val observer = LifecycleEventObserver { _, event ->
            when (event) {

                Lifecycle.Event.ON_START -> {
                    // Resume GPS and the workout timer.
                    startTracking()
                    workoutTimer.resume()
                }

                Lifecycle.Event.ON_STOP -> {
                    // Pause GPS and the timer while in the background.
                    stopTracking()
                    workoutTimer.pause()
                }

                else -> Unit
            }
        }


        lifecycleOwner.lifecycle.addObserver(observer)

        // Start even if the activity is already visible.
        if (lifecycleOwner.lifecycle.currentState.isAtLeast(
                Lifecycle.State.STARTED
            )
        ) {
            startTracking()
        }

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            stopTracking()
        }
    }


    // Keep the workout timer connected to the workout stage.
    LaunchedEffect(workoutStage.intValue) {

        when (workoutStage.intValue) {


            2 -> {
                // Record the actual date and time the workout begins.
                workoutStartTime.longValue = System.currentTimeMillis()

                // Start a new timer when the workout begins.
                workoutTimer.start()
                elapsedMillis.longValue = 0L

                // Update the displayed time while working out.
                while (true) {
                    elapsedMillis.longValue =
                        workoutTimer.getElapsedTime()

                    delay(250L)
                }
            }


            3 -> {
                // Stop the timer and save its final duration.
                elapsedMillis.longValue = workoutTimer.finish()
            }
        }
    }


    // Message shown if location permission is not granted.
    val locationPermissionMessage = remember {
        mutableStateOf<String?>(null)
    }

    // Requests location permissions from Android.
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->

        val preciseGranted =
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true

        val approximateGranted =
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        when {
            preciseGranted -> {
                locationPermissionMessage.value = null
                workoutStage.intValue = 2
            }


            approximateGranted -> {
                locationPermissionMessage.value =
                    context.getString(R.string.location_precise_required)
            }

            else -> {
                locationPermissionMessage.value =
                    context.getString(R.string.location_permission_required)
            }

        }
    }


    // 0 = Home
    // 1 = Workout Setup
    // 2 = Active Workout
    // 3 = Workout Completed

    val goal_enabled = remember { mutableStateOf(false) }
    val activity_options = listOf("Walk", "Run")
    val selected_activity = remember { mutableStateOf(0) }
    val goal_distance = remember { mutableStateOf("") }

    val validGoal = !goal_enabled.value ||
            (goal_distance.value.matches(Regex("[0-9]+(\\.[0-9]+)?")) &&
                    goal_distance.value.toDoubleOrNull()?.let {
                        it > 0 && it.isFinite()
                    } == true)

    AnimatedContent(
        targetState = workoutStage.intValue,
        modifier = Modifier.fillMaxSize(),
        label = "Workout screen transition",
        transitionSpec = {
            if (targetState > initialState) {
                (slideInVertically { height -> height } + fadeIn()) togetherWith
                        (slideOutVertically { height -> -height } + fadeOut())
            } else {
                (slideInVertically { height -> -height } + fadeIn()) togetherWith
                        (slideOutVertically { height -> height } + fadeOut())
            }
        }
    ) { stage ->

        when (stage) {

            // SCREEN 1: Home
            0 -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = {
                                val intent = Intent(
                                    context,
                                    SettingsActivity::class.java
                                )
                                context.startActivity(intent)
                            }
                        ) {
                            Text("⚙ " + stringResource(R.string.settings_title))
                        }
                    }


                    Spacer(modifier = Modifier.height(80.dp))

                    WelcomeMessage()

                    Spacer(modifier = Modifier.weight(1f))

                    OutlinedButton(
                        onClick = {
                            val intent = Intent(
                                context,
                                HistoryActivity::class.java
                            )

                            val options = ActivityOptions.makeCustomAnimation(
                                context,
                                R.anim.slide_in_up,
                                0
                            )

                            context.startActivity(intent, options.toBundle())
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text(stringResource(R.string.home_view_history))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            workoutStage.intValue = 1
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text(stringResource(R.string.home_start_workout))
                    }
                }
            }

            // SCREEN 2: Workout setup
            1 -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Text(
                        text = stringResource(R.string.workout_setup_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            GoalControls(
                                goal_enabled,
                                activity_options,
                                selected_activity,
                                goal_distance
                            )
                        }
                    }

                    if (!validGoal) {

                        Text(stringResource(R.string.workout_invalid_goal))

                    }

                    Button(

                        onClick = {
                            val hasPreciseLocation =
                                ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ) == PackageManager.PERMISSION_GRANTED

                            if (hasPreciseLocation) {
                                locationPermissionMessage.value = null
                                workoutStage.intValue = 2
                            } else {
                                locationPermissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                        },

                        enabled = validGoal,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {

                        val activityLabel = if (selected_activity.value == 0) {
                            stringResource(R.string.history_walk)
                        } else {
                            stringResource(R.string.history_run)
                        }

                        Text(
                            text = stringResource(
                                R.string.workout_begin_activity,
                                activityLabel
                            )
                        )

                    }


                    if (locationPermissionMessage.value != null) {

                        Text(
                            text = locationPermissionMessage.value ?: ""
                        )

                        OutlinedButton(
                            onClick = {
                                val intent = Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                ).apply {
                                    data = Uri.fromParts(
                                        "package",
                                        context.packageName,
                                        null
                                    )
                                }

                                context.startActivity(intent)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Text(stringResource(R.string.location_open_settings))

                        }
                    }


                    OutlinedButton(
                        onClick = {
                            workoutStage.intValue = 0
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.settings_back_home))
                    }
                }
            }

            // SCREEN 3: Active workout
            2 -> {
                // Read the direction the phone is facing.
                val phoneHeading = rememberPhoneHeading()
                // Set an initial location for testing the map.
                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(
                        LatLng(53.3498, -6.2603),
                        14f
                    )
                }
                // Remember whether Google Maps has finished loading.
                val mapLoaded = remember { mutableStateOf(false) }
                // Follow the latest GPS location during the workout.
                LaunchedEffect(mapLoaded.value, latestGpsLocation.value) {
                    val location = latestGpsLocation.value
                    if (mapLoaded.value && location != null) {
                        val currentPosition = LatLng(
                            location.latitude,
                            location.longitude
                        )
                        cameraPositionState.animate(
                            update = CameraUpdateFactory.newLatLngZoom(
                                currentPosition,
                                17f
                            ),
                            durationMs = 700
                        )
                    }
                }
                // Allow the map and workout controls to overlap.
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        uiSettings = MapUiSettings(
                            zoomControlsEnabled = false
                        ),
                        onMapLoaded = {
                            mapLoaded.value = true
                        }
                    ) {
                    // Display the current GPS location.
                        latestGpsLocation.value?.let { location ->
                            val currentPosition = LatLng(
                                location.latitude,
                                location.longitude
                            )
                            MarkerComposable(
                                state = rememberUpdatedMarkerState(
                                    position = currentPosition
                                ),
                                anchor = Offset(0.5f, 0.5f),
                                flat = true,
                                rotation = phoneHeading,
                                title = "Current Location",
                                contentDescription = "Current location arrow"
                            ) {
                                // White circular background for better map visibility.
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(Color.White, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Blue navigation arrow.
                                    Text(
                                        text = "▲",
                                        color = Color(0xFF1565C0),
                                        fontSize = 30.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            // Convert GPS route points into map coordinates.
                            val routeCoordinates =
                                locationTracker.getRoutePoints().map { point ->
                                    LatLng(
                                        point.latitude,
                                        point.longitude
                                    )
                                }
                            // Draw the route as a blue line.
                            if (routeCoordinates.size >= 2) {
                                Polyline(
                                    points = routeCoordinates,
                                    color = Color(0xFF1565C0),
                                    width = 12f
                                )
                            }
                        }
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = if (selected_activity.value == 0) {
                                stringResource(R.string.workout_walk_session)
                            } else {
                                stringResource(R.string.workout_run_session)
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 26.sp
                        )
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 6.dp
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Workout time
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = stringResource(R.string.history_time).uppercase(),
                                            fontSize = 12.sp,
                                            color = Color.DarkGray
                                        )
                                        Text(
                                            text = formatElapsedTime(elapsedMillis.longValue),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = Color(0xFF17324D)
                                        )
                                    }
                                    // GPS distance
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = stringResource(R.string.history_distance).uppercase(),
                                            fontSize = 12.sp,
                                            color = Color.DarkGray
                                        )
                                        Text(
                                            text = "%.2f km".format(distanceMeters.value / 1000f),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = Color(0xFF17324D)
                                        )
                                    }
                                    // Average pace
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = stringResource(R.string.history_pace).uppercase(),
                                            fontSize = 12.sp,
                                            color = Color.DarkGray
                                        )
                                        Text(
                                            text = formatPace(
                                                calculateAveragePaceSecondsPerKm(
                                                    elapsedMillis.longValue,
                                                    distanceMeters.value
                                                )
                                            ),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = Color(0xFF17324D)
                                        )
                                    }
                                }
                                if (goal_enabled.value && validGoal) {
                                    HorizontalDivider()
                                    Text(stringResource(R.string.workout_distance_goal))
                                    Text("${goal_distance.value} km")
                                }
                            }
                        }
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 4.dp
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                // Show the current GPS tracking status.
                                Text(
                                    text = when {
                                        gpsError.value != null ->
                                            gpsError.value ?: ""
                                        latestGpsLocation.value == null ->
                                            stringResource(R.string.gps_waiting)
                                        else ->
                                            stringResource(R.string.gps_tracking)
                                    },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (gpsError.value != null) {
                                        Color(0xFFB91C1C)
                                    } else {
                                        Color(0xFF17324D)
                                    }
                                )
                                // Explain which statistics are unfinished.
                                Text(
                                    text = stringResource(R.string.workout_live_stats),
                                    fontSize = 12.sp,
                                    color = Color.DarkGray
                                )
                            }
                        }
                        // Push the Finish button to the bottom of the screen.
                        Spacer(modifier = Modifier.weight(1f))
                        Button(
                            onClick = {
                                // Stop the timer and record the final duration.
                                elapsedMillis.longValue = workoutTimer.finish()
                                // Save the completed workout to Liam's Room database.
                                workoutViewModel.saveWorkout(
                                    WorkoutSession(
                                        activityType = activity_options[selected_activity.value],
                                        startTime = workoutStartTime.longValue,
                                        durationMillis = elapsedMillis.longValue,
                                        distanceMetres = distanceMeters.value.toDouble(),
                                        averagePaceSecondsPerKm =
                                            calculateAveragePaceSecondsPerKm(
                                                elapsedMillis.longValue,
                                                distanceMeters.value
                                            ),
                                        distanceGoalKm = if (goal_enabled.value && validGoal) {
                                            goal_distance.value.toDoubleOrNull()
                                        } else {
                                            null
                                        }
                                    )
                                )
                                // Navigate to the completed workout screen.
                                workoutStage.intValue = 3
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text(
                                text = stringResource(R.string.workout_finish),
                                fontWeight = FontWeight.Bold
                            )
                        }

                    }
                }
            }

            // SCREEN 4: Workout completed
            3 -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.workout_completed_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp
                    )
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            val activityLabel = if (selected_activity.value == 0) {
                                stringResource(R.string.history_walk)
                            } else {
                                stringResource(R.string.history_run)
                            }
                            Text(
                                text = stringResource(
                                    R.string.workout_summary_activity,
                                    activityLabel
                                )
                            )
                            Text(
                                text = stringResource(
                                    R.string.workout_summary_elapsed,
                                    formatElapsedTime(elapsedMillis.longValue)
                                )
                            )
                            Text(
                                text = stringResource(
                                    R.string.workout_summary_distance,
                                    "%.2f".format(distanceMeters.value / 1000f)
                                )
                            )
                            Text(
                                text = stringResource(
                                    R.string.workout_summary_pace,
                                    formatPace(
                                        calculateAveragePaceSecondsPerKm(
                                            elapsedMillis.longValue,
                                            distanceMeters.value
                                        )
                                    )
                                )
                            )
                            if (goal_enabled.value && validGoal) {
                                Text(
                                    text = stringResource(
                                        R.string.workout_summary_goal,
                                        goal_distance.value
                                    )
                                )
                            }
                        }
                    }
                    Text(
                        text = stringResource(R.string.workout_saved_message)
                    )
                    Button(
                        onClick = {
                            workoutStage.intValue = 1
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.workout_new))
                    }
                    OutlinedButton(
                        onClick = {
                            workoutStage.intValue = 0
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.settings_back_home))
                    }
                }
            }
        }
    }
}




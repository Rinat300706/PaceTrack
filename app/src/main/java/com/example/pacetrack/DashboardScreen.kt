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
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import androidx.compose.runtime.LaunchedEffect
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberUpdatedMarkerState
import com.google.maps.android.compose.Polyline
import androidx.compose.ui.graphics.Color


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

        Text("Track your walks and runs")
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
        text = "Choose your activity",
        fontWeight = FontWeight.Bold
    )

    RadioButtonGroup(
        activity_options,
        selected_activity.value
    ) {
        selected_activity.value = it
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Enable distance goal",
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
            text = "Distance Target (km)",
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

        Text("Your distance goal is: ${goal_distance.value} km")
    }
}


@Composable
fun DashboardScreen() {

    val context = LocalContext.current

    val workoutStage = remember { mutableIntStateOf(0) }

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
    DisposableEffect(workoutStage.intValue, locationTracker) {

        if (workoutStage.intValue == 2) {

            locationTracker.resetDistance()
            latestGpsLocation.value = null
            distanceMeters.value = 0f

            val started = locationTracker.start()

            gpsError.value = if (started) {
                null
            } else {
                "GPS could not start. Check that location is enabled."
            }
        }

        onDispose {
            locationTracker.stop()
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
                    "Precise location is needed for accurate workout distance. " +
                            "Enable precise location for PaceTrack in your phone settings."
            }

            else -> {
                locationPermissionMessage.value =
                    "Location permission is required to track your workout distance."
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
                        Text("View History")
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
                        Text("Start Workout")
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
                        text = "Workout Setup",
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
                        Text("Enter a valid distance greater than 0 km.")
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
                        Text("Begin ${activity_options[selected_activity.value]}")
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
                            Text("Open Location Settings")
                        }
                    }


                    OutlinedButton(
                        onClick = {
                            workoutStage.intValue = 0
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Back to Home")
                    }
                }
            }

            // SCREEN 3: Active workout
            2 -> {

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


                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Text(
                        text = "${activity_options[selected_activity.value]} Session",
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {

                            Text("Elapsed Time")

                            Text(
                                text = "--:--:--",
                                fontWeight = FontWeight.Bold,
                                fontSize = 32.sp
                            )

                            HorizontalDivider()

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Distance")

                                    Text(
                                        text = "%.2f km".format(distanceMeters.value / 1000f),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    )

                                }

                                Column {
                                    Text("Average Pace")
                                    Text(
                                        "-- /km",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    )
                                }
                            }

                            if (goal_enabled.value && validGoal) {
                                HorizontalDivider()
                                Text("Distance Goal")
                                Text("${goal_distance.value} km")
                            }
                        }
                    }


                    // Display an interactive Google Map

                    GoogleMap(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp),
                        cameraPositionState = cameraPositionState,
                        onMapLoaded = {
                            mapLoaded.value = true
                        }
                    ) {

                        // Display a marker at the latest GPS location.
                        latestGpsLocation.value?.let { location ->

                            val currentPosition = LatLng(
                                location.latitude,
                                location.longitude
                            )

                            Marker(
                                state = rememberUpdatedMarkerState(
                                    position = currentPosition
                                ),
                                title = "Current Location"
                            )

                            // Convert recorded GPS locations into map coordinates.
                            val routeCoordinates = locationTracker.getRoutePoints().map { point ->
                                LatLng(
                                    point.latitude,
                                    point.longitude
                                )
                            }

                            // Draw a blue line when we have at least two GPS points.
                            if (routeCoordinates.size >= 2) {
                                Polyline(
                                    points = routeCoordinates,
                                    color = Color(0xFF1565C0),
                                    width = 12f
                                )
                            }
                        }
                    }




                    Text(
                        text = when {
                            gpsError.value != null -> gpsError.value ?: ""

                            latestGpsLocation.value == null ->
                                "GPS: Waiting for location..."

                            else -> "GPS: Location received"
                        }
                    )

                    Text("Timer and pace are still previews.")


                    Button(
                        onClick = {
                            workoutStage.intValue = 3
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text("Finish Preview")
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
                        text = "Workout Preview Finished",
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

                            Text("Activity: ${activity_options[selected_activity.value]}")

                            Text("Elapsed Time: --:--:--")


                            Text(
                                text = "Distance: %.2f km".format(distanceMeters.value / 1000f)
                            )


                            Text("Average Pace: -- /km")

                            if (goal_enabled.value && validGoal) {
                                Text("Distance Goal: ${goal_distance.value} km")
                            }
                        }
                    }


                    Text(
                        "Your GPS distance was tracked during this workout. " +
                                "The session has not been saved to History yet."
                    )


                    Button(
                        onClick = {
                            workoutStage.intValue = 1
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("New Workout")
                    }

                    OutlinedButton(
                        onClick = {
                            workoutStage.intValue = 0
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Back to Home")
                    }
                }
            }
        }
    }
}




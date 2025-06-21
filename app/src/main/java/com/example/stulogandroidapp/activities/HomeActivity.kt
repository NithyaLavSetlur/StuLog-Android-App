package com.example.stulogandroidapp.activities

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val navView = findViewById<BottomNavigationView>(R.id.bottomNav)
        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHost.navController

        NavigationUI.setupWithNavController(navView, navController)
    }
}

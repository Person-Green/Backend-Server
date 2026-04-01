CREATE TABLE IF NOT EXISTS recommendation_plant (
    plant_id VARCHAR(30) PRIMARY KEY,
    name_ko VARCHAR(100) NOT NULL,
    name_en VARCHAR(100) NOT NULL,
    difficulty VARCHAR(30) NOT NULL,
    pet_safety VARCHAR(30) NOT NULL,
    air_purification_level VARCHAR(30) NOT NULL,
    size_category VARCHAR(30) NOT NULL,
    display_water_cycle VARCHAR(100) NOT NULL,
    display_temp_range VARCHAR(100) NOT NULL,
    display_humidity_range VARCHAR(100) NOT NULL,
    display_light_requirement VARCHAR(100) NOT NULL,
    one_line_description VARCHAR(500) NOT NULL,
    recommended_location_text VARCHAR(500) NOT NULL
);

CREATE TABLE IF NOT EXISTS recommendation_plant_condition (
    plant_id VARCHAR(30) PRIMARY KEY,
    sunlight_level VARCHAR(30) NOT NULL,
    ventilation_need VARCHAR(30) NOT NULL,
    temp_min INT NOT NULL,
    temp_max INT NOT NULL,
    humidity_min INT NOT NULL,
    humidity_max INT NOT NULL,
    water_cycle_min_days INT NOT NULL,
    water_cycle_max_days INT NOT NULL,
    CONSTRAINT fk_recommendation_plant_condition_plant
        FOREIGN KEY (plant_id) REFERENCES recommendation_plant(plant_id)
);

CREATE TABLE IF NOT EXISTS recommendation_plant_env_fit (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    plant_id VARCHAR(30) NOT NULL,
    environment_type VARCHAR(50) NOT NULL,
    fit_level VARCHAR(30) NOT NULL,
    CONSTRAINT fk_recommendation_plant_env_fit_plant
        FOREIGN KEY (plant_id) REFERENCES recommendation_plant(plant_id)
);

CREATE TABLE IF NOT EXISTS recommendation_plant_placement (
    plant_id VARCHAR(30) NOT NULL,
    placement_type VARCHAR(30) NOT NULL,
    PRIMARY KEY (plant_id, placement_type),
    CONSTRAINT fk_recommendation_plant_placement_plant
        FOREIGN KEY (plant_id) REFERENCES recommendation_plant_condition(plant_id)
);

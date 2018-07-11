DROP procedure IF EXISTS add_movie;

DELIMITER $$
CREATE PROCEDURE add_movie(IN movie_title VARCHAR(100), IN movie_year INT, IN movie_director VARCHAR(100), IN movie_star VARCHAR(100), IN movie_genre VARCHAR(32), IN movie_rating FLOAT)
BEGIN
	if( select not exists (select * from movies where title=movie_title)) then
		# find the max string1
		select (@max_movie_id := max(id)) m_id from movies;
		SELECT (@max_movie_id_trim:=TRIM(LEADING 'tt' FROM @max_movie_id));
		SELECT @max_movie_id_trim:=CAST(@max_movie_id_trim AS UNSIGNED INT);
        SET @max_movie_id_trim:= @max_movie_id_trim+1;
        if(@max_movie_id_trim < 1000000) then
			select (@max_movie_id_trim:=cast(@max_movie_id_trim as char(7)));
			SELECT @final_movie_id:=CONCAT('tt0', @max_movie_id_trim);
            INSERT INTO movies (id,title,year,director) VALUES (@final_movie_id, movie_title, movie_year, movie_director);
		else
			select (@max_movie_id_trim:=cast(@max_movie_id_trim as char(8)));
			SELECT @final_movie_id:=CONCAT('tt', @max_movie_id_trim);
            INSERT INTO movies (id,title,year,director) VALUES (@final_movie_id, movie_title, movie_year, movie_director);
		end if;
        #add rating to that movie if movie did not exist
        select @rating_movieID:=id from movies where title = movie_title;
        insert into ratings (movieID, rating, numVotes) values(@rating_movieID, movie_rating,1);
    end if;
    # if star is not in database, add it to stars and stars_in_movies
    if( select not exists (select *  from stars where name = movie_star)) then
		select (@max_star_id := max(id)) m_id from stars;
        select (@max_star_id := TRIM(LEADING 'nm' FROM @max_star_id));
        select @max_star_id := CAST(@max_star_id as unsigned int);
        set @max_star_id := @max_star_id + 1;
        if(@max_star_id < 1000000) then
			select (@max_star_id:=cast(@max_star_id as char(7)));
            select @final_star_id := concat('nm0', @max_star_id);
            INSERT INTO stars (id, name) VALUES(@final_star_id,movie_star);
            INSERT INTO stars_in_movies (starID, movieID) VALUES(@final_star_id,@final_movie_id);
		else
			select (@max_star_id:=cast(@max_star_id as char(8)));
            select @final_star_id := concat('nm', @max_star_id);
            INSERT INTO stars (id, name) VALUES(@final_star_id,movie_star);
            INSERT INTO stars_in_movies (starID, movieID) VALUES(@final_star_id,@final_movie_id);
		end if;
	else 
		select @star_id:=id from stars where name = movie_star;
        select @movie:=id from movies where title = movie_title;
        if (select not exists (select * from stars_in_movies where starID = @star_id and movieID = @movie)) then 
			INSERT INTO stars_in_movies (starID, movieID) VALUES(@star_id,@movie);
        end if;
	end if;
    # add genre if it does not exist in database, otherwise, link it to 
    if(select not exists (select * from genres where name = movie_genre)) then 
		select @movie_id2 := id from movies where title = movie_title;
        Insert into genres (name) values(movie_genre);
        select @genre_id := id from genres where name = movie_genre;
        Insert into genres_in_movies (genreID,movieID) values(@genre_id,@movie_id2);
	else
		select @genre_id := id from genres where name = movie_genre;
        select @movie_id2 := id from movies where title = movie_title;
        if(select not exists (select * from genres_in_movies where genreID = @genre_id and movieID = @movie_id2)) then
			INSERT INTO genres_in_movies (genreID,movieID) VALUES(@genre_id,@movie_id2);
		end if;
    end if;
END$$

DELIMITER ;
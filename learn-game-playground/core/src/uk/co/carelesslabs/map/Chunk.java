package uk.co.carelesslabs.map;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Chunk {
    int numberRows;
    int numberCols;
    int tileSize;
    // Tiles are split into arrays of rows
    public Array<Array<Tile>> tiles = new Array<Array<Tile>>();
    
    public Chunk(int number_rows, int number_cols, int tile_size ){
        tiles = new Array<Array<Tile>>();
        this.numberRows = number_rows;
        this.numberCols = number_cols;
        this.tileSize = tile_size;
    }
    
    public Tile getTile(int row, int col){
        Array<Tile> chunk_row;
        if(tiles.size > row && row >= 0){
            chunk_row = tiles.get(row);
        
            if(chunk_row != null && chunk_row.size > col && col >= 0){
                return chunk_row.get(col);
            }
        }
        return null;
    }

    public String getTileCode(int row, int col){
        Tile tile;

        Array<Tile> chunk_row;
        if(tiles.size > row && row >= 0){
            chunk_row = tiles.get(row);
        
            if(chunk_row != null && chunk_row.size > col && col >= 0){
                tile = chunk_row.get(col);
                return tile.isGrass() ? "1" : "0";
            }
        }
        return "0";
    }

    public Tile getTile(Vector2 vector2) {
        Array<Tile> chunk_row;
        int row = (int) ((vector2.y*tileSize/2) / numberRows);
        int col = (int) ((vector2.x*tileSize/2) / numberCols);
        if(tiles.size > row && row >= 0){
            chunk_row = tiles.get(row);
        
            if(chunk_row != null && chunk_row.size > col && col >= 0){
                return chunk_row.get(col);
            }
        }
        return null;
    }

}
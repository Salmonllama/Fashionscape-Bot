/*
 * Copyright (c) 2020. Aleksei Gryczewski
 * All rights reserved.
 */

package dev.salmonllama.fsbot.database.controllers;

import dev.salmonllama.fsbot.database.FSDB;
import dev.salmonllama.fsbot.database.models.Outfit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class OutfitController {
    public static CompletableFuture<Void> insert(Outfit outfit) {
        return CompletableFuture.runAsync(() -> {
            try {
                insertExec(outfit);
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }

    public static CompletableFuture<Optional<Outfit>> findById(String id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return findByIdExec(id);
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }

    public static CompletableFuture<Optional<Outfit>> findRandom() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return findRandomExec();
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }

    public static CompletableFuture<Optional<Outfit>> findRandomByTag(String tag) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return findRandomByTagExec(tag);
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }

    public static CompletableFuture<Optional<Outfit>> findRandomBySubmitter(String submitterId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return findRandomBySubmitterExec(submitterId);
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }

    public static CompletableFuture<Integer> countOutfits() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return countOutfitsExec();
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }

    public static CompletableFuture<Integer> countOutfitsBySubmitter(String submitterId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return countOutfitsBySubmitterExec(submitterId);
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }

    private static void insertExec(Outfit outfit) throws SQLException {
        if (outfit.getCreated() == null) {
            outfit.setCreated(new Timestamp(System.currentTimeMillis()));
        }

        if (outfit.getUpdated() == null) {
            outfit.setUpdated(new Timestamp(System.currentTimeMillis()));
        }

        FSDB.get().insert(
                "INSERT INTO " +
                        "outfits('id', 'link', 'submitter', 'tag', 'created', 'updated', 'deleted', 'featured', 'display_count', 'delete_hash') " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                outfit.getId(),
                outfit.getLink(),
                outfit.getSubmitter(),
                outfit.getTag(),
                outfit.getCreated(),
                outfit.getUpdated(),
                outfit.isDeleted(),
                outfit.isFeatured(),
                outfit.getDisplayCount(),
                outfit.getDeleteHash()
                );
    }

    private static Optional<Outfit> findByIdExec(String id) throws SQLException {
        ResultSet rs = FSDB.get().select("SELECT * FROM outfits WHERE id = ?", id);

        if (rs.next()) {
            Outfit outfit = mapObject(rs);
            FSDB.get().close(rs);
            return Optional.of(outfit);
        }

        FSDB.get().close(rs);
        return Optional.empty();
    }

    private static Optional<Outfit> findRandomExec() throws SQLException {
        ResultSet rs = FSDB.get().select("SELECT * FROM outfits WHERE deleted = 0 ORDER BY random() LIMIT 1");

        if (rs.next()) {
            Outfit outfit = mapObject(rs);
            FSDB.get().close(rs);
            return Optional.of(outfit);
        }

        FSDB.get().close(rs);
        return Optional.empty();
    }

    private static Optional<Outfit> findRandomByTagExec(String tag) throws SQLException {
        ResultSet rs = FSDB.get().select("SELECT * FROM outfits WHERE tag = ? AND deleted = 0 ORDER BY random() LIMIT 1", tag);

        if (rs.next()) {
            Outfit outfit = mapObject(rs);
            FSDB.get().close(rs);
            return Optional.of(outfit);
        }

        FSDB.get().close(rs);
        return Optional.empty();
    }

    private static Optional<Outfit> findRandomBySubmitterExec(String submitterId) throws SQLException {
        ResultSet rs = FSDB.get().select("SELECT * FROM outfits WHERE submitter = ? AND deleted = 0 ORDER BY random() LIMIT 1", submitterId);

        if (rs.next()) {
            Outfit outfit = mapObject(rs);
            FSDB.get().close(rs);
            return Optional.of(outfit);
        }

        FSDB.get().close(rs);
        return Optional.empty();
    }

    private static int countOutfitsExec() throws SQLException {
        ResultSet rs = FSDB.get().select("SELECT COUNT(*) AS count FROM outfits WHERE deleted = 0");

        int count = 0;
        if (rs.next()) {
            count = rs.getInt("count");
        }

        FSDB.get().close(rs);
        return count;
    }

    private static int countOutfitsBySubmitterExec(String submitterId) throws SQLException {
        ResultSet rs = FSDB.get().select("SELECT COUNT(*) AS count FROM outfits WHERE submitter = ? AND deleted = 0", submitterId);

        int count = 0;
        if (rs.next()) {
            count = rs.getInt("count");
        }

        FSDB.get().close(rs);
        return count;
    }

    private static Outfit mapObject(ResultSet rs) throws SQLException {
        return new Outfit.OutfitBuilder()
                .setId(rs.getString("id"))
                .setLink(rs.getString("link"))
                .setSubmitter(rs.getString("submitter"))
                .setTag(rs.getString("tag"))
                .setCreated(new Timestamp(rs.getLong("created")))
                .setUpdated(new Timestamp((rs.getLong("updated"))))
                .setDeleted(rs.getBoolean("deleted"))
                .setFeatured(rs.getBoolean("featured"))
                .setDisplayCount(rs.getInt("display_count"))
                .setDeleteHash(rs.getString("delete_hash"))
                .build();
    }
}
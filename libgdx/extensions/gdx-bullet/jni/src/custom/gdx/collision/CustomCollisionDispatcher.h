#ifdef _MSC_VER
#pragma once
#endif

#ifndef CustomCollisionDispatcher_H
#define CustomCollisionDispatcher_H

/**
 * For some reason swig doesnt see the needsCollision/needsResponse inheritance (it does see the other virtual methods do).
*/
class CustomCollisionDispatcher : public btCollisionDispatcher {
public:
    CustomCollisionDispatcher(btCollisionConfiguration *collisionConfiguration)
            : btCollisionDispatcher(collisionConfiguration) {}

    virtual bool needsCollision(const btCollisionObject *body0, const btCollisionObject *body1) {
        return btCollisionDispatcher::needsCollision(body0, body1);
    }

    virtual bool needsResponse(const btCollisionObject *body0, const btCollisionObject *body1) {
        return btCollisionDispatcher::needsResponse(body0, body1);
    }
};

#endif //CustomCollisionDispatcher_H

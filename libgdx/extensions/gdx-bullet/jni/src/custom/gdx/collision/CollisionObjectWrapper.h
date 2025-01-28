#ifndef CollisionObjectWrapper_H
#define CollisionObjectWrapper_H

#include "../../../bullet/BulletCollision/CollisionDispatch/btCollisionObject.h"
#include "../../../bullet/BulletCollision/CollisionDispatch/btCollisionObjectWrapper.h"

class CollisionObjectWrapper {
	btCollisionObjectWrapper wrapper;

public:
	CollisionObjectWrapper(btCollisionObjectWrapper* parent, btCollisionShape* shape, btCollisionObject* collisionObject, btTransform& worldTransform, int partId=-1, int index=-1) :
		wrapper(parent, shape, collisionObject, worldTransform, partId, index) {}

	CollisionObjectWrapper(btCollisionShape* shape, btCollisionObject* collisionObject, btTransform& worldTransform, int partId=-1, int index=-1) :
		wrapper(0, shape, collisionObject, worldTransform, partId, index) {}

	CollisionObjectWrapper(btCollisionObjectWrapper* parent, btCollisionObject* collisionObject, int partId=-1, int index=-1) :
		wrapper(parent, collisionObject->getCollisionShape(),collisionObject, collisionObject->getWorldTransform(), partId, index) {}

	CollisionObjectWrapper(btCollisionObject* collisionObject, int partId=-1, int index=-1) :
		wrapper(0, collisionObject->getCollisionShape(),collisionObject, collisionObject->getWorldTransform(), partId, index) {}

	btCollisionObjectWrapper *getWrapper() { return &wrapper; }
};

#endif //CollisionObjectWrapper_H

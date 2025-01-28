#ifdef _MSC_VER
#pragma once
#endif

#ifndef ContactCache_H
#define ContactCache_H

#include "ContactListener.h"
#include "../../../bullet/LinearMath/btAlignedObjectArray.h"

#ifndef SWIG
struct ContactPair;
#endif //SWIG


class ContactCache {
protected:
#ifndef SWIG
	bool filter;
	int events;
	btAlignedObjectArray<ContactPair> cache;
	//std::map<ContactPair, float> cache;
#endif
public:
	float cacheTime;

	ContactCache(bool dummy);
	virtual ~ContactCache();

#ifndef SWIG
	void setEvents(const int &events);
#endif //SWIG

	void enable();
	void disable();
	bool isEnabled();

	//virtual void onContactStarted(btPersistentManifold* manifold) = 0;
	//virtual void onContactStarted(const btCollisionObject* colObj0, const btCollisionObject* colObj1) = 0;
	//virtual void onContactStarted(const int &userValue0, const int &userValue1) = 0;

	virtual void onContactStarted(btPersistentManifold* manifold, const bool &match0, const bool &match1) = 0;
	//virtual void onContactStarted(const btCollisionObject* colObj0, const bool &match0, const btCollisionObject* colObj1, const bool &match1) = 0;
	//virtual void onContactStarted(const int &userValue0, const bool &match0, const int &userValue1, const bool &match1) = 0;

	//virtual void onContactEnded(btPersistentManifold* manifold) = 0;
	//virtual void onContactEnded(const btCollisionObject* colObj0, const btCollisionObject* colObj1) = 0;
	//virtual void onContactEnded(const int &userValue0, const int &userValue1) = 0;

	//virtual void onContactEnded(btPersistentManifold* manifold, const bool &match0, const bool &match1) = 0;
	virtual void onContactEnded(const btCollisionObject* colObj0, const bool &match0, const btCollisionObject* colObj1, const bool &match1) = 0;
	//virtual void onContactEnded(const int &userValue0, const bool &match0, const int &userValue1, const bool &match1) = 0;

	void clear();

	void update(float delta);

#ifndef SWIG
	int indexOf(const btCollisionObject* const &obj0, const btCollisionObject* const &obj1);

	void contactStarted(btPersistentManifold* manifold);

	void contactEnded(btPersistentManifold* manifold);
#endif //SWIG
};

#endif //ContactCache_H
